package com.wallaceespindola.modernization.interest;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Parallel Run: computes both results, records the comparison, and always serves the legacy one. */
public class ParallelRun implements InterestCalculator {

    private static final int DAYS_IN_LEGACY_MONTH = 30;
    private static final BigDecimal ONE_CENT = new BigDecimal("0.01");

    private final InterestCalculator legacy;
    private final InterestCalculator modern;
    private final BigDecimal tolerance;
    private final List<Comparison> comparisons = new CopyOnWriteArrayList<>();

    public ParallelRun(InterestCalculator legacy, InterestCalculator modern, BigDecimal tolerance) {
        this.legacy = legacy;
        this.modern = modern;
        this.tolerance = tolerance;
    }

    @Override
    public BigDecimal monthlyInterest(BigDecimal balance, BigDecimal annualRate, YearMonth month) {
        BigDecimal legacyResult = legacy.monthlyInterest(balance, annualRate, month);
        BigDecimal modernResult = modern.monthlyInterest(balance, annualRate, month);
        BigDecimal difference = modernResult.subtract(legacyResult).abs();
        comparisons.add(new Comparison(balance, annualRate, month, legacyResult, modernResult, difference,
                classify(difference, month)));
        return legacyResult;
    }

    /**
     * Heuristic, in order: equal is MATCH; one cent apart is ROUNDING noise; up to the tolerance is
     * WITHIN_TOLERANCE; a bigger gap in a month that is not 30 days long is DAY_COUNT; the rest needs a human.
     */
    private MismatchCategory classify(BigDecimal difference, YearMonth month) {
        if (difference.signum() == 0) {
            return MismatchCategory.MATCH;
        }
        if (difference.compareTo(ONE_CENT) <= 0) {
            return MismatchCategory.ROUNDING;
        }
        if (difference.compareTo(tolerance) <= 0) {
            return MismatchCategory.WITHIN_TOLERANCE;
        }
        boolean oddMonthLength = month.lengthOfMonth() != DAYS_IN_LEGACY_MONTH;
        return oddMonthLength ? MismatchCategory.DAY_COUNT : MismatchCategory.UNEXPLAINED;
    }

    public List<Comparison> comparisons() {
        return List.copyOf(comparisons);
    }

    public ParallelRunReport report() {
        return ParallelRunReport.from(comparisons());
    }
}
