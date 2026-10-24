package com.wallaceespindola.modernization.outbox;

/** Transactional Outbox: the broker side of the relay, anything that can fail. */
@FunctionalInterface
public interface Publisher {

    void publish(OutboxEvent event);
}
