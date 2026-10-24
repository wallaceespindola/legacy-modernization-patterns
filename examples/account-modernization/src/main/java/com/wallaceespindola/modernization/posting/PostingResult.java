package com.wallaceespindola.modernization.posting;

/** Safe posting: the outcome is either known to be posted or explicitly unknown, never guessed. */
public sealed interface PostingResult {

    record Posted(String idempotencyKey, String reference) implements PostingResult {
    }

    record Unknown(String idempotencyKey) implements PostingResult {
    }
}
