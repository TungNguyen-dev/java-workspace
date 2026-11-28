package tungnn.tutor.java.tool.finance.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.tool.finance.core.validate.AmountGuard;

class AmountUtilsTest {

    @Test
    void shouldParseVietnameseAndEnglishFormats() {
        assertThat(AmountUtils.parse("1.234.567")).contains(new BigDecimal("1234567"));
        assertThat(AmountUtils.parse("1,234,567")).contains(new BigDecimal("1234567"));
        assertThat(AmountUtils.parse("500.000đ")).contains(new BigDecimal("500000"));
        assertThat(AmountUtils.parse("VND 2,500,000")).contains(new BigDecimal("2500000"));
        assertThat(AmountUtils.parse("1.234,56")).contains(new BigDecimal("1234.56"));
        assertThat(AmountUtils.parse("-500.000")).contains(new BigDecimal("-500000"));
        assertThat(AmountUtils.parse("abc")).isEmpty();
    }

    @Test
    void guardShouldAcceptOnlyAmountsPresentInTheText() {
        var guard = new AmountGuard("TK 1234 -500.000 VND luc 10:30, so du 12,345,678 VND");

        assertThat(guard.isPresent(new BigDecimal("500000"))).isTrue();
        assertThat(guard.isPresent(new BigDecimal("12345678"))).isTrue();
        assertThat(guard.isPresent(new BigDecimal("600000"))).isFalse();
    }
}
