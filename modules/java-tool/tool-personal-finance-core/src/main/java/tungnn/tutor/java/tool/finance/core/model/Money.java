package tungnn.tutor.java.tool.finance.core.model;

import java.math.BigDecimal;
import java.util.Objects;

public record Money(BigDecimal amount, String currency) {

    public static final String VND = "VND";

    public Money {
        Objects.requireNonNull(amount, "amount must not be null");
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount must not be negative: " + amount);
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("currency must not be blank");
        }
    }

    public static Money vnd(long amount) {
        return new Money(BigDecimal.valueOf(amount), VND);
    }
}
