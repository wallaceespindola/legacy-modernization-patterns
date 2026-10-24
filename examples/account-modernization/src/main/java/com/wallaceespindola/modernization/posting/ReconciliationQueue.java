package com.wallaceespindola.modernization.posting;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Safe posting: parking lot for postings whose outcome is still unknown, drained by reconciliation. */
public class ReconciliationQueue {

    private final List<PostingRequest> pending = new CopyOnWriteArrayList<>();

    public void add(PostingRequest request) {
        pending.add(request);
    }

    public List<PostingRequest> pending() {
        return List.copyOf(pending);
    }
}
