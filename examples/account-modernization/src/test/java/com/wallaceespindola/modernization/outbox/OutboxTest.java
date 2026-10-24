package com.wallaceespindola.modernization.outbox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

class OutboxTest {

    private final InMemoryUnitOfWork store = new InMemoryUnitOfWork();

    private void debit(String account) {
        store.execute(work -> {
            work.ledgerEntry("DEBIT " + account);
            work.outbox(OutboxEvent.of(account, "AccountDebited", "{}"));
        });
    }

    @Test
    void execute_commit_storesLedgerEntryAndEventTogether() {
        debit("ACC1");

        assertThat(store.ledger()).containsExactly("DEBIT ACC1");
        assertThat(store.unpublished()).hasSize(1);
    }

    @Test
    void execute_failureAfterBothWrites_leavesNoLedgerEntryAndNoEvent() {
        assertThatThrownBy(() -> store.execute(work -> {
            work.ledgerEntry("DEBIT ACC1");
            work.outbox(OutboxEvent.of("ACC1", "AccountDebited", "{}"));
            throw new IllegalStateException("boom");
        })).isInstanceOf(IllegalStateException.class);

        assertThat(store.ledger()).isEmpty();
        assertThat(store.unpublished()).isEmpty();
    }

    @Test
    void relayOnce_publisherFailsThenRecovers_resendsSameEvent() {
        debit("ACC1");
        List<OutboxEvent> received = new ArrayList<>();
        AtomicBoolean broken = new AtomicBoolean(true);
        OutboxRelay relay = new OutboxRelay(store, event -> {
            if (broken.get()) {
                throw new IllegalStateException("broker down");
            }
            received.add(event);
        });

        assertThat(relay.relayOnce()).isZero();
        assertThat(store.unpublished()).hasSize(1);

        broken.set(false);
        assertThat(relay.relayOnce()).isEqualTo(1);
        assertThat(received).hasSize(1);
        assertThat(store.unpublished()).isEmpty();
        assertThat(relay.relayOnce()).isZero();
    }

    @Test
    void relayOnce_severalEvents_publishesInCommitOrder() {
        debit("ACC1");
        debit("ACC2");
        List<String> order = new ArrayList<>();

        new OutboxRelay(store, event -> order.add(event.aggregateId())).relayOnce();

        assertThat(order).containsExactly("ACC1", "ACC2");
    }

    @Test
    void accept_duplicateDelivery_handlesEventOnce() {
        debit("ACC1");
        OutboxEvent event = store.unpublished().getFirst();
        List<OutboxEvent> handled = new ArrayList<>();
        IdempotentConsumer consumer = new IdempotentConsumer(handled::add);

        assertThat(consumer.accept(event)).isTrue();
        assertThat(consumer.accept(event)).isFalse();
        assertThat(handled).hasSize(1);
    }

    @Test
    void accept_handlerFails_eventIsNotMarkedProcessed() {
        OutboxEvent event = OutboxEvent.of("ACC1", "AccountDebited", "{}");
        AtomicBoolean fail = new AtomicBoolean(true);
        IdempotentConsumer consumer = new IdempotentConsumer(e -> {
            if (fail.get()) {
                throw new IllegalStateException("handler down");
            }
        });

        assertThatThrownBy(() -> consumer.accept(event)).isInstanceOf(IllegalStateException.class);
        fail.set(false);
        assertThat(consumer.accept(event)).isTrue();
    }
}
