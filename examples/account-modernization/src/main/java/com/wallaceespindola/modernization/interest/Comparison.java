package com.wallaceespindola.modernization.interest;

import java.math.BigDecimal;
import java.time.YearMonth;

/** Parallel Run: one recorded comparison of the legacy and modern results for the same inputs. */
public record Comparison(
        BigDecimal balance,
        BigDecimal annualRate,
        YearMonth month,
        BigDecimal legacy,
        BigDecimal modern,
        BigDecimal difference,
        MismatchCategory category) {
}
