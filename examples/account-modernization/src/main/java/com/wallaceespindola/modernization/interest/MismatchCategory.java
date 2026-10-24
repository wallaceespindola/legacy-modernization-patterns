package com.wallaceespindola.modernization.interest;

/** Parallel Run: the triage bucket for a legacy versus modern difference. */
public enum MismatchCategory {
    MATCH,
    WITHIN_TOLERANCE,
    ROUNDING,
    DAY_COUNT,
    UNEXPLAINED
}
