package com.wallaceespindola.modernization.outbox;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

/** Transactional Outbox: polling relay with at-least-once delivery, marks published only after success. */
public class OutboxRelay {

    private static final Logger LOG = System.getLogger(OutboxRelay.class.getName());

    private final InMemoryUnitOfWork store;
    private final Publisher publisher;

    public OutboxRelay(InMemoryUnitOfWork store, Publisher publisher) {
        this.store = store;
        this.publisher = publisher;
    }

    /** Publishes pending events in order and stops at the first failure to keep the order. */
    public int relayOnce() {
        int sent = 0;
        for (OutboxEvent event : store.unpublished()) {
            try {
                publisher.publish(event);
            } catch (RuntimeException e) {
                LOG.log(Level.WARNING, "Publish failed for {0}, will retry: {1}", event.eventId(), e.getMessage());
                break;
            }
            store.markPublished(event.eventId());
            sent++;
        }
        return sent;
    }
}
