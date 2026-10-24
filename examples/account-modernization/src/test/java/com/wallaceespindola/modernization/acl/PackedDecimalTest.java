package com.wallaceespindola.modernization.acl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PackedDecimalTest {

    @Test
    void decode_positiveSignC_returnsScaledValue() {
        byte[] bytes = {0x01, 0x23, 0x45, 0x6C};
        assertThat(PackedDecimal.decode(bytes, 2)).isEqualByComparingTo(new BigDecimal("1234.56"));
    }

    @Test
    void decode_signF_isPositive() {
        assertThat(PackedDecimal.decode(new byte[] {0x12, 0x3F}, 0)).isEqualByComparingTo("123");
    }

    @Test
    void decode_signD_isNegative() {
        assertThat(PackedDecimal.decode(new byte[] {0x12, 0x5D}, 2)).isEqualByComparingTo("-1.25");
    }

    @Test
    void decode_invalidDigitNibble_throws() {
        assertThatThrownBy(() -> PackedDecimal.decode(new byte[] {0x1A, 0x2C}, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void decode_invalidSignNibble_throws() {
        assertThatThrownBy(() -> PackedDecimal.decode(new byte[] {0x12, 0x35}, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void decode_emptyInput_throws() {
        assertThatThrownBy(() -> PackedDecimal.decode(new byte[0], 0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
