package com.wallaceespindola.modernization.interest;

import java.math.BigDecimal;
import java.time.YearMonth;

/** Branch by Abstraction: a runtime switch between legacy, shadow (parallel run) and modern. */
public class InterestCalculatorToggle implements InterestCalculator {

    public enum Mode { LEGACY, SHADOW, MODERN }

    private final InterestCalculator legacy;
    private final InterestCalculator modern;
    private final ParallelRun shadow;
    private volatile Mode mode;

    public InterestCalculatorToggle(Mode mode, InterestCalculator legacy, InterestCalculator modern,
                                    BigDecimal tolerance) {
        this.mode = mode;
        this.legacy = legacy;
        this.modern = modern;
        this.shadow = new ParallelRun(legacy, modern, tolerance);
    }

    @Override
    public BigDecimal monthlyInterest(BigDecimal balance, BigDecimal annualRate, YearMonth month) {
        InterestCalculator target = switch (mode) {
            case LEGACY -> legacy;
            case SHADOW -> shadow;
            case MODERN -> modern;
        };
        return target.monthlyInterest(balance, annualRate, month);
    }

    public void switchTo(Mode newMode) {
        this.mode = newMode;
    }

    public ParallelRun parallelRun() {
        return shadow;
    }
}
