package tungnn.tutor.java.tool.finance.core.dedupe;

import java.time.temporal.ChronoUnit;
import tungnn.tutor.java.tool.finance.core.model.Transaction;

public final class TransactionMatcher {

    private TransactionMatcher() {
        throw new UnsupportedOperationException("cannot instantiate utility class");
    }

    public static boolean matches(Transaction a, Transaction b) {
        if (!a.accountId().equals(b.accountId())
                || a.direction() != b.direction()
                || !a.money().currency().equals(b.money().currency())
                || a.money().amount().compareTo(b.money().amount()) != 0) {
            return false;
        }
        var bothHaveReference = hasText(a.reference()) && hasText(b.reference());
        if (a.sourceId().equals(b.sourceId())) {
            // Rows of one source are distinct purchases unless the bank reference proves they are the same one.
            return bothHaveReference && a.reference().equalsIgnoreCase(b.reference());
        }
        if (bothHaveReference) {
            return a.reference().equalsIgnoreCase(b.reference());
        }
        return Math.abs(ChronoUnit.DAYS.between(a.bookedOn(), b.bookedOn())) <= 1;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
