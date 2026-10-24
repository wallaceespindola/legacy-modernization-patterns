package com.wallaceespindola.modernization.interest;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;

class InterestCalculatorsTest {

    private static final YearMonth FEBRUARY = YearMonth.of(2026, 2);
    private static final YearMonth JANUARY = YearMonth.of(2026, 1);
    private final InterestCalculator legacy = new LegacyInterestCalculator();
    private final InterestCalculator modern = new ModernInterestCalculator();

    @Test
    void legacy_anyMonthLength_usesThirtyDays() {
        BigDecimal balance = new BigDecimal("1000.00");
        BigDecimal rate = new BigDecimal("0.05");
        assertThat(legacy.monthlyInterest(balance, rate, FEBRUARY)).isEqualByComparingTo("4.17");
        assertThat(legacy.monthlyInterest(balance, rate, JANUARY)).isEqualByComparingTo("4.17");
    }

    @Test
    void modern_februaryVs31DayMonth_usesActualDays() {
        BigDecimal balance = new BigDecimal("1000.00");
        BigDecimal rate = new BigDecimal("0.05");
        assertThat(modern.monthlyInterest(balance, rate, FEBRUARY)).isEqualByComparingTo("3.84");
        assertThat(modern.monthlyInterest(balance, rate, JANUARY)).isEqualByComparingTo("4.25");
    }

    @Test
    void legacy_exactTie_roundsHalfUp() {
        // 1700 * 0.015 * 30 / 360 = 2.1250 exactly
        assertThat(legacy.monthlyInterest(new BigDecimal("1700"), new BigDecimal("0.015"), JANUARY))
                .isEqualByComparingTo("2.13");
    }

    @Test
    void modern_exactTie_roundsHalfEven() {
        // 12775 * 0.0001 * 30 / 365 = 0.105 exactly, half-up would give 0.11
        assertThat(modern.monthlyInterest(new BigDecimal("12775"), new BigDecimal("0.0001"), YearMonth.of(2026, 4)))
                .isEqualByComparingTo("0.10");
    }

    @Test
    void legacy_truncatesIntermediateResult() {
        // raw 0.0041 stays 0.0041 (not rounded up to 0.0042) before the final rounding
        assertThat(legacy.monthlyInterest(new BigDecimal("1.00"), new BigDecimal("0.05"), JANUARY))
                .isEqualByComparingTo("0.00");
    }
}
