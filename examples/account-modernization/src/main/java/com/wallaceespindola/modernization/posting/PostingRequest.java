package com.wallaceespindola.modernization.posting;

import java.math.BigDecimal;

/** Safe posting: a financial write that carries its idempotency key end to end. */
public record PostingRequest(String idempotencyKey, String accountId, BigDecimal amount) {
}
