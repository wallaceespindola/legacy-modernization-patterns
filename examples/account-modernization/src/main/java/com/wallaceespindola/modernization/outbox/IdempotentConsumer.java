package com.wallaceespindola.modernization.outbox;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

/** Idempotent Consumer: an inbox of processed event ids that makes redelivery harmless. */
public class IdempotentConsumer {

    private final Set<UUID> inbox = new HashSet<>();
    private final Consumer<OutboxEvent> handler;

    public IdempotentConsumer(Consumer<OutboxEvent> handler) {
        this.handler = handler;
    }

    /** Returns true when the event was handled, false when it was a duplicate. */
    public synchronized boolean accept(OutboxEvent event) {
        if (inbox.contains(event.eventId())) {
            return false;
        }
        handler.accept(event);
        inbox.add(event.eventId());
        return true;
    }
}
