package com.wallaceespindola.modernization.posting;

import com.wallaceespindola.modernization.posting.PostingResult.Posted;
import com.wallaceespindola.modernization.posting.PostingResult.Unknown;
import java.util.Optional;

/** Safe posting: on timeout, ask the ledger before retrying, retry once, then reconcile. */
public class SafePostingClient {

    private final PostingGateway gateway;
    private final ReconciliationQueue reconciliation;

    public SafePostingClient(PostingGateway gateway, ReconciliationQueue reconciliation) {
        this.gateway = gateway;
        this.reconciliation = reconciliation;
    }

    public PostingResult post(PostingRequest request) {
        try {
            return gateway.post(request);
        } catch (PostingTimeoutException timeout) {
            return resolveUnknownOutcome(request);
        }
    }

    private PostingResult resolveUnknownOutcome(PostingRequest request) {
        Optional<PostingResult> alreadyPosted = lookup(request);
        if (alreadyPosted.isPresent()) {
            return alreadyPosted.get();
        }
        try {
            return gateway.post(request);
        } catch (PostingTimeoutException timeout) {
            return lookup(request).orElseGet(() -> park(request));
        }
    }

    private Optional<PostingResult> lookup(PostingRequest request) {
        return gateway.status(request.idempotencyKey())
                .map(status -> new Posted(status.idempotencyKey(), status.reference()));
    }

    private PostingResult park(PostingRequest request) {
        reconciliation.add(request);
        return new Unknown(request.idempotencyKey());
    }
}
