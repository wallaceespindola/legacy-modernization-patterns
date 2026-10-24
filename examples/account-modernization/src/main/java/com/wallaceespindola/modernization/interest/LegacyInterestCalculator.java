package com.wallaceespindola.modernization.interest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;

/** Branch by Abstraction: legacy behavior, 30/360, truncated to 4 decimals, then ROUNDED half-up. */
public class LegacyInterestCalculator implements InterestCalculator {

    private static final int DAYS_IN_MONTH = 30;
    private static final int DAYS_IN_YEAR = 360;
    private static final int WORK_SCALE = 4;
    private static final int RESULT_SCALE = 2;

    @Override
    public BigDecimal monthlyInterest(BigDecimal balance, BigDecimal annualRate, YearMonth month) {
        BigDecimal raw = balance.multiply(annualRate)
                .multiply(BigDecimal.valueOf(DAYS_IN_MONTH))
                .divide(BigDecimal.valueOf(DAYS_IN_YEAR), WORK_SCALE, RoundingMode.DOWN);
        return raw.setScale(RESULT_SCALE, RoundingMode.HALF_UP);
    }
}
