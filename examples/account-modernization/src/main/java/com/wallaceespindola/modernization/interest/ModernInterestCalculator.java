package com.wallaceespindola.modernization.interest;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.YearMonth;

/** Branch by Abstraction: replacement, ACT/365, no intermediate truncation, half-even. */
public class ModernInterestCalculator implements InterestCalculator {

    private static final int DAYS_IN_YEAR = 365;
    private static final int RESULT_SCALE = 2;

    @Override
    public BigDecimal monthlyInterest(BigDecimal balance, BigDecimal annualRate, YearMonth month) {
        return balance.multiply(annualRate)
                .multiply(BigDecimal.valueOf(month.lengthOfMonth()))
                .divide(BigDecimal.valueOf(DAYS_IN_YEAR), MathContext.DECIMAL128)
                .setScale(RESULT_SCALE, RoundingMode.HALF_EVEN);
    }
}
