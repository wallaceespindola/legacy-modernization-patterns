package com.wallaceespindola.modernization.acl;

import static com.wallaceespindola.modernization.acl.LegacyAccountRecord.BALANCE_LENGTH;
import static com.wallaceespindola.modernization.acl.LegacyAccountRecord.EBCDIC;
import static com.wallaceespindola.modernization.acl.LegacyAccountRecord.ID_LENGTH;

import java.io.ByteArrayOutputStream;

/** Test helper: builds a 20-byte EBCDIC record the way the mainframe would write it. */
final class RecordBuilder {

    private RecordBuilder() {
    }

    static byte[] record(String id, char type, long cents, String returnCode) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.writeBytes(String.format("%-" + ID_LENGTH + "s", id).getBytes(EBCDIC));
        out.writeBytes(String.valueOf(type).getBytes(EBCDIC));
        out.writeBytes(packed(cents, BALANCE_LENGTH));
        out.writeBytes(returnCode.getBytes(EBCDIC));
        return out.toByteArray();
    }

    static byte[] packed(long value, int length) {
        String digits = String.format("%0" + (length * 2 - 1) + "d", Math.abs(value));
        String hex = digits + (value < 0 ? "D" : "C");
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return bytes;
    }
}
