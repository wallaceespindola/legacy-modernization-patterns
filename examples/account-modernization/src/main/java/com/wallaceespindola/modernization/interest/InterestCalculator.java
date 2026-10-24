package com.wallaceespindola.modernization.interest;

import java.math.BigDecimal;
import java.time.YearMonth;

/** Branch by Abstraction: the seam that hides which implementation computes the interest. */
public interface InterestCalculator {

    BigDecimal monthlyInterest(BigDecimal balance, BigDecimal annualRate, YearMonth month);
}
