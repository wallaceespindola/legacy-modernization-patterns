package com.wallaceespindola.modernization.outbox;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** Transactional Outbox: ledger rows and outbox rows commit together or not at all. */
public class InMemoryUnitOfWork {

    /** The working copy a transaction body writes to. */
    public static final class Work {
        private final List<String> ledger;
        private final List<OutboxEvent> outbox;

        private Work(List<String> ledger, List<OutboxEvent> outbox) {
            this.ledger = new ArrayList<>(ledger);
            this.outbox = new ArrayList<>(outbox);
        }

        public void ledgerEntry(String entry) {
            ledger.add(entry);
        }

        public void outbox(OutboxEvent event) {
            outbox.add(event);
        }
    }

    private final ReentrantLock lock = new ReentrantLock();
    private final Set<UUID> published = new LinkedHashSet<>();
    private List<String> ledger = List.of();
    private List<OutboxEvent> outbox = List.of();

    public void execute(Consumer<Work> body) {
        lock.lock();
        try {
            Work work = new Work(ledger, outbox);
            body.accept(work);
            ledger = List.copyOf(work.ledger);
            outbox = List.copyOf(work.outbox);
        } finally {
            lock.unlock();
        }
    }

    public List<String> ledger() {
        return locked(() -> ledger);
    }

    public List<OutboxEvent> unpublished() {
        return locked(() -> outbox.stream().filter(e -> !published.contains(e.eventId())).toList());
    }

    public void markPublished(UUID eventId) {
        locked(() -> published.add(eventId));
    }

    private <T> T locked(Supplier<T> action) {
        lock.lock();
        try {
            return action.get();
        } finally {
            lock.unlock();
        }
    }
}
