package com.wallaceespindola.modernization.acl;

import java.math.BigDecimal;
import java.math.BigInteger;

/** Anti-Corruption Layer: decodes COBOL COMP-3 packed decimals into BigDecimal. */
public final class PackedDecimal {

    private static final int SIGN_POSITIVE_C = 0xC;
    private static final int SIGN_POSITIVE_F = 0xF;
    private static final int SIGN_NEGATIVE = 0xD;
    private static final int MAX_DIGIT = 9;

    private PackedDecimal() {
    }

    public static BigDecimal decode(byte[] bytes, int scale) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("Empty packed decimal");
        }
        BigInteger digits = BigInteger.ZERO;
        int last = bytes.length - 1;
        for (int i = 0; i <= last; i++) {
            digits = append(digits, bytes[i] >> 4 & 0xF);
            if (i < last) {
                digits = append(digits, bytes[i] & 0xF);
            }
        }
        boolean negative = isNegative(bytes[last] & 0xF);
        return new BigDecimal(negative ? digits.negate() : digits, scale);
    }

    private static BigInteger append(BigInteger digits, int nibble) {
        if (nibble > MAX_DIGIT) {
            throw new IllegalArgumentException("Invalid packed digit nibble: " + nibble);
        }
        return digits.multiply(BigInteger.TEN).add(BigInteger.valueOf(nibble));
    }

    private static boolean isNegative(int signNibble) {
        return switch (signNibble) {
            case SIGN_POSITIVE_C, SIGN_POSITIVE_F -> false;
            case SIGN_NEGATIVE -> true;
            default -> throw new IllegalArgumentException("Invalid packed sign nibble: " + signNibble);
        };
    }
}
