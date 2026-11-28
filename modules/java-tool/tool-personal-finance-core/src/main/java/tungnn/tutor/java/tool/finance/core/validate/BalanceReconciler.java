package tungnn.tutor.java.tool.finance.core.validate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import tungnn.tutor.java.tool.finance.core.model.Direction;
import tungnn.tutor.java.tool.finance.core.model.Transaction;

public final class BalanceReconciler {

    private BalanceReconciler() {
        throw new UnsupportedOperationException("cannot instantiate utility class");
    }

    public static Optional<String> check(BigDecimal opening, BigDecimal closing, List<Transaction> transactions) {
        if (opening == null || closing == null) {
            return Optional.empty();
        }
        var net = transactions.stream()
                .map(t -> t.direction() == Direction.CREDIT ? t.money().amount() : t.money().amount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var expected = opening.add(net);
        if (expected.compareTo(closing) == 0) {
            return Optional.empty();
        }
        return Optional.of("Balance mismatch: opening %s + net %s = %s but closing is %s"
                .formatted(opening.toPlainString(), net.toPlainString(), expected.toPlainString(), closing.toPlainString()));
    }
}
