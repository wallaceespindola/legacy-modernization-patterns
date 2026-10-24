package com.wallaceespindola.modernization.acl;

/** Anti-Corruption Layer: legacy return codes expressed as domain outcomes. */
public sealed interface AccountLookup {

    record Found(AccountBalance balance) implements AccountLookup {
    }

    record NotFound(String accountId) implements AccountLookup {
    }

    record Blocked(String accountId) implements AccountLookup {
    }
}
