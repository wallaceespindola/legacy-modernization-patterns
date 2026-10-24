package com.wallaceespindola.modernization.acl;

import static com.wallaceespindola.modernization.acl.RecordBuilder.record;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wallaceespindola.modernization.acl.AccountLookup.Blocked;
import com.wallaceespindola.modernization.acl.AccountLookup.Found;
import com.wallaceespindola.modernization.acl.AccountLookup.NotFound;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class AccountTranslatorTest {

    private final AccountTranslator translator = new AccountTranslator();

    @Test
    void translate_ledgerRecord_returnsPostedBalance() {
        AccountLookup result = translator.translate(record("ACC0000001", 'L', 123456, "00"));
        assertThat(result).isEqualTo(
                new Found(new AccountBalance("ACC0000001", new BigDecimal("1234.56"), BalanceType.POSTED)));
    }

    @Test
    void translate_memoPostedRecord_returnsPendingBalance() {
        AccountLookup result = translator.translate(record("ACC0000001", 'M', 5000, "00"));
        assertThat(result).isInstanceOfSatisfying(Found.class,
                found -> assertThat(found.balance().type()).isEqualTo(BalanceType.PENDING));
    }

    @Test
    void translate_negativeBalance_keepsSign() {
        AccountLookup result = translator.translate(record("ACC0000001", 'L', -250, "00"));
        assertThat(result).isInstanceOfSatisfying(Found.class,
                found -> assertThat(found.balance().amount()).isEqualByComparingTo("-2.50"));
    }

    @Test
    void translate_returnCode04_returnsNotFound() {
        assertThat(translator.translate(record("ACC0000002", 'L', 0, "04"))).isEqualTo(new NotFound("ACC0000002"));
    }

    @Test
    void translate_returnCode08_returnsBlocked() {
        assertThat(translator.translate(record("ACC0000003", 'L', 0, "08"))).isEqualTo(new Blocked("ACC0000003"));
    }

    @Test
    void translate_otherReturnCode_throwsTechnicalException() {
        assertThatThrownBy(() -> translator.translate(record("ACC0000004", 'L', 0, "99")))
                .isInstanceOf(LegacyTechnicalException.class).hasMessageContaining("99");
    }

    @Test
    void translate_unknownTypeFlag_throwsTechnicalException() {
        assertThatThrownBy(() -> translator.translate(record("ACC0000005", 'X', 100, "00")))
                .isInstanceOf(LegacyTechnicalException.class);
    }

    @Test
    void translate_wrongLength_throwsTechnicalException() {
        assertThatThrownBy(() -> translator.translate(new byte[5])).isInstanceOf(LegacyTechnicalException.class);
    }

    @Test
    void translate_corruptPackedBalance_throwsTechnicalException() {
        byte[] raw = record("ACC0000006", 'L', 100, "00");
        raw[LegacyAccountRecord.ID_LENGTH + 1] = (byte) 0xFF;
        assertThatThrownBy(() -> translator.translate(raw)).isInstanceOf(LegacyTechnicalException.class);
    }

    @Test
    void parse_builtRecord_isEbcdicEncoded() {
        byte[] raw = record("A", 'L', 1, "00");
        assertThat(raw).hasSize(LegacyAccountRecord.LENGTH);
        assertThat(raw[0]).isEqualTo((byte) 0xC1); // 'A' in IBM037
    }
}
