package com.wallaceespindola.modernization.outbox;

import java.time.Instant;
import java.util.UUID;

/** Transactional Outbox: the event row stored next to the business change. */
public record OutboxEvent(UUID eventId, String aggregateId, String type, String payload, Instant occurredAt) {

    public static OutboxEvent of(String aggregateId, String type, String payload) {
        return new OutboxEvent(UUID.randomUUID(), aggregateId, type, payload, Instant.now());
    }
}
