package com.wallaceespindola.modernization.interest;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Parallel Run: counts of comparisons per mismatch category, for the triage conversation. */
public record ParallelRunReport(Map<MismatchCategory, Long> counts) {

    public static ParallelRunReport from(List<Comparison> comparisons) {
        Map<MismatchCategory, Long> counts = new EnumMap<>(MismatchCategory.class);
        for (MismatchCategory category : MismatchCategory.values()) {
            counts.put(category, 0L);
        }
        comparisons.forEach(c -> counts.merge(c.category(), 1L, Long::sum));
        return new ParallelRunReport(counts);
    }

    public long count(MismatchCategory category) {
        return counts.get(category);
    }

    public long total() {
        return counts.values().stream().mapToLong(Long::longValue).sum();
    }
}
