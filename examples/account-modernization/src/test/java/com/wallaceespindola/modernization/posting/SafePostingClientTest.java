package com.wallaceespindola.modernization.posting;

import static org.assertj.core.api.Assertions.assertThat;

import com.wallaceespindola.modernization.posting.PostingResult.Posted;
import com.wallaceespindola.modernization.posting.PostingResult.Unknown;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SafePostingClientTest {

    private static final PostingRequest REQUEST = new PostingRequest("key-1", "ACC1", new BigDecimal("10.00"));
    private final ReconciliationQueue queue = new ReconciliationQueue();

    @Test
    void post_noTimeout_returnsPosted() {
        FakeGateway gateway = new FakeGateway(0, false);

        PostingResult result = new SafePostingClient(gateway, queue).post(REQUEST);

        assertThat(result).isInstanceOf(Posted.class);
        assertThat(gateway.attemptedKeys).containsExactly("key-1");
    }

    @Test
    void post_timeoutButPosted_returnsStatusWithoutSecondPost() {
        FakeGateway gateway = new FakeGateway(1, true);

        PostingResult result = new SafePostingClient(gateway, queue).post(REQUEST);

        assertThat(result).isEqualTo(new Posted("key-1", "REF-key-1"));
        assertThat(gateway.attemptedKeys).containsExactly("key-1");
        assertThat(queue.pending()).isEmpty();
    }

    @Test
    void post_timeoutNotPosted_retriesOnceWithSameKey() {
        FakeGateway gateway = new FakeGateway(1, false);

        PostingResult result = new SafePostingClient(gateway, queue).post(REQUEST);

        assertThat(result).isInstanceOf(Posted.class);
        assertThat(gateway.attemptedKeys).containsExactly("key-1", "key-1");
        assertThat(gateway.ledger).hasSize(1);
    }

    @Test
    void post_timeoutTwiceAndStillUnknown_queuesForReconciliation() {
        FakeGateway gateway = new FakeGateway(2, false);

        PostingResult result = new SafePostingClient(gateway, queue).post(REQUEST);

        assertThat(result).isEqualTo(new Unknown("key-1"));
        assertThat(queue.pending()).containsExactly(REQUEST);
        assertThat(gateway.attemptedKeys).hasSize(2);
        assertThat(gateway.ledger).isEmpty();
    }

    @Test
    void post_retryTimesOutButLedgerHadApplied_returnsPostedWithoutThirdAttempt() {
        FakeGateway gateway = new FakeGateway(2, false);
        gateway.applyOnSecondTimeout = true;

        PostingResult result = new SafePostingClient(gateway, queue).post(REQUEST);

        assertThat(result).isInstanceOf(Posted.class);
        assertThat(gateway.attemptedKeys).hasSize(2);
        assertThat(gateway.ledger).hasSize(1);
        assertThat(queue.pending()).isEmpty();
    }

    /** Idempotent fake ledger: the first N calls time out, optionally after applying the posting. */
    private static final class FakeGateway implements PostingGateway {
        final List<String> attemptedKeys = new ArrayList<>();
        final Map<String, PostingStatus> ledger = new HashMap<>();
        private int timeoutsLeft;
        private final boolean applyOnFirstTimeout;
        boolean applyOnSecondTimeout;

        FakeGateway(int timeouts, boolean applyOnFirstTimeout) {
            this.timeoutsLeft = timeouts;
            this.applyOnFirstTimeout = applyOnFirstTimeout;
        }

        @Override
        public PostingResult post(PostingRequest request) {
            attemptedKeys.add(request.idempotencyKey());
            if (timeoutsLeft > 0) {
                boolean first = attemptedKeys.size() == 1;
                if (first ? applyOnFirstTimeout : applyOnSecondTimeout) {
                    apply(request);
                }
                timeoutsLeft--;
                throw new PostingTimeoutException("timeout");
            }
            PostingStatus status = apply(request);
            return new Posted(status.idempotencyKey(), status.reference());
        }

        @Override
        public Optional<PostingStatus> status(String idempotencyKey) {
            return Optional.ofNullable(ledger.get(idempotencyKey));
        }

        private PostingStatus apply(PostingRequest request) {
            return ledger.computeIfAbsent(request.idempotencyKey(),
                    key -> new PostingStatus(key, "REF-" + key));
        }
    }
}
