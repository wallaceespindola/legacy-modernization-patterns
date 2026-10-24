package com.wallaceespindola.modernization.interest;

import static org.assertj.core.api.Assertions.assertThat;

import com.wallaceespindola.modernization.interest.InterestCalculatorToggle.Mode;
import java.math.BigDecimal;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;

class ParallelRunTest {

    private static final BigDecimal RATE = new BigDecimal("0.05");
    private static final BigDecimal TOLERANCE = new BigDecimal("0.01");
    private final ParallelRun run = new ParallelRun(
            new LegacyInterestCalculator(), new ModernInterestCalculator(), TOLERANCE);

    @Test
    void monthlyInterest_shadowRun_servesLegacyAndRecordsComparison() {
        BigDecimal served = run.monthlyInterest(new BigDecimal("1000"), RATE, YearMonth.of(2026, 1));

        assertThat(served).isEqualByComparingTo("4.17");
        assertThat(run.comparisons()).singleElement().satisfies(c -> {
            assertThat(c.legacy()).isEqualByComparingTo("4.17");
            assertThat(c.modern()).isEqualByComparingTo("4.25");
            assertThat(c.difference()).isEqualByComparingTo("0.08");
            assertThat(c.category()).isEqualTo(MismatchCategory.DAY_COUNT);
        });
    }

    @Test
    void report_mixedInputs_countsEachCategory() {
        run.monthlyInterest(BigDecimal.ZERO, RATE, YearMonth.of(2026, 1));
        run.monthlyInterest(new BigDecimal("100"), RATE, YearMonth.of(2026, 4));
        run.monthlyInterest(new BigDecimal("1000"), RATE, YearMonth.of(2026, 2));
        run.monthlyInterest(new BigDecimal("1000"), RATE, YearMonth.of(2026, 4));

        ParallelRunReport report = run.report();

        assertThat(report.count(MismatchCategory.MATCH)).isEqualTo(1);
        assertThat(report.count(MismatchCategory.ROUNDING)).isEqualTo(1);
        assertThat(report.count(MismatchCategory.DAY_COUNT)).isEqualTo(1);
        assertThat(report.count(MismatchCategory.UNEXPLAINED)).isEqualTo(1);
        assertThat(report.count(MismatchCategory.WITHIN_TOLERANCE)).isZero();
        assertThat(report.total()).isEqualTo(4);
    }

    @Test
    void monthlyInterest_widerTolerance_classifiesSmallGapAsWithinTolerance() {
        ParallelRun lenient = new ParallelRun(
                new LegacyInterestCalculator(), new ModernInterestCalculator(), new BigDecimal("0.10"));

        lenient.monthlyInterest(new BigDecimal("1000"), RATE, YearMonth.of(2026, 4));

        assertThat(lenient.report().count(MismatchCategory.WITHIN_TOLERANCE)).isEqualTo(1);
    }

    @Test
    void toggle_eachMode_servesTheExpectedImplementation() {
        InterestCalculatorToggle toggle = new InterestCalculatorToggle(
                Mode.LEGACY, new LegacyInterestCalculator(), new ModernInterestCalculator(), TOLERANCE);
        YearMonth january = YearMonth.of(2026, 1);
        BigDecimal balance = new BigDecimal("1000");

        assertThat(toggle.monthlyInterest(balance, RATE, january)).isEqualByComparingTo("4.17");
        assertThat(toggle.parallelRun().comparisons()).isEmpty();

        toggle.switchTo(Mode.SHADOW);
        assertThat(toggle.monthlyInterest(balance, RATE, january)).isEqualByComparingTo("4.17");
        assertThat(toggle.parallelRun().comparisons()).hasSize(1);

        toggle.switchTo(Mode.MODERN);
        assertThat(toggle.monthlyInterest(balance, RATE, january)).isEqualByComparingTo("4.25");
        assertThat(toggle.parallelRun().comparisons()).hasSize(1);
    }
}
