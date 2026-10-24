package com.wallaceespindola.modernization.acl;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Arrays;

/** Anti-Corruption Layer: the fixed-width EBCDIC copybook record, parsed but not yet interpreted. */
public record LegacyAccountRecord(String accountId, char typeFlag, BigDecimal balance, String returnCode) {

    public static final Charset EBCDIC = Charset.forName("IBM037");
    public static final int ID_LENGTH = 10;
    public static final int BALANCE_LENGTH = 7;
    public static final int RETURN_CODE_LENGTH = 2;
    public static final int LENGTH = ID_LENGTH + 1 + BALANCE_LENGTH + RETURN_CODE_LENGTH;
    private static final int BALANCE_SCALE = 2;

    public static LegacyAccountRecord parse(byte[] raw) {
        if (raw == null || raw.length != LENGTH) {
            throw new LegacyTechnicalException("Unexpected record length, expected " + LENGTH);
        }
        int typeAt = ID_LENGTH;
        int balanceAt = typeAt + 1;
        int codeAt = balanceAt + BALANCE_LENGTH;
        try {
            return new LegacyAccountRecord(
                    new String(raw, 0, ID_LENGTH, EBCDIC).trim(),
                    new String(raw, typeAt, 1, EBCDIC).charAt(0),
                    PackedDecimal.decode(Arrays.copyOfRange(raw, balanceAt, codeAt), BALANCE_SCALE),
                    new String(raw, codeAt, RETURN_CODE_LENGTH, EBCDIC));
        } catch (IllegalArgumentException e) {
            throw new LegacyTechnicalException("Corrupt legacy record", e);
        }
    }
}
