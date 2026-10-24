package com.wallaceespindola.modernization.posting;

/** Safe posting: what the ledger knows about a key, answer to the status inquiry. */
public record PostingStatus(String idempotencyKey, String reference) {
}
