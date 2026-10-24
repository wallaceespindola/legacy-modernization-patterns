package com.wallaceespindola.modernization.acl;

import java.math.BigDecimal;

/** Anti-Corruption Layer: the clean domain balance, free of legacy field names and codes. */
public record AccountBalance(String accountId, BigDecimal amount, BalanceType type) {
}
