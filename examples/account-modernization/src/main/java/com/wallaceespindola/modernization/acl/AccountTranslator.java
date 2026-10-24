package com.wallaceespindola.modernization.acl;

import com.wallaceespindola.modernization.acl.AccountLookup.Blocked;
import com.wallaceespindola.modernization.acl.AccountLookup.Found;
import com.wallaceespindola.modernization.acl.AccountLookup.NotFound;

/** Anti-Corruption Layer: semantic translation of a raw legacy record into a domain result. */
public class AccountTranslator {

    private static final String OK = "00";
    private static final String NOT_FOUND = "04";
    private static final String BLOCKED = "08";
    private static final char LEDGER_FLAG = 'L';
    private static final char MEMO_POSTED_FLAG = 'M';

    public AccountLookup translate(byte[] raw) {
        LegacyAccountRecord record = LegacyAccountRecord.parse(raw);
        return switch (record.returnCode()) {
            case OK -> new Found(new AccountBalance(record.accountId(), record.balance(), typeOf(record.typeFlag())));
            case NOT_FOUND -> new NotFound(record.accountId());
            case BLOCKED -> new Blocked(record.accountId());
            default -> throw new LegacyTechnicalException("Legacy return code " + record.returnCode());
        };
    }

    private static BalanceType typeOf(char flag) {
        return switch (flag) {
            case LEDGER_FLAG -> BalanceType.POSTED;
            case MEMO_POSTED_FLAG -> BalanceType.PENDING;
            default -> throw new LegacyTechnicalException("Unknown record type flag " + flag);
        };
    }
}
