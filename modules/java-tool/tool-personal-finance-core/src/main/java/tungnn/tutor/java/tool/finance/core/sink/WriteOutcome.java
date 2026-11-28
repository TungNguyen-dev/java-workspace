package tungnn.tutor.java.tool.finance.core.sink;

import java.util.Objects;
import tungnn.tutor.java.tool.finance.core.model.Transaction;

public record WriteOutcome(Transaction transaction, Status status, String reason) {

    public enum Status {
        WRITTEN,
        FAILED
    }

    public WriteOutcome {
        Objects.requireNonNull(transaction, "transaction must not be null");
        Objects.requireNonNull(status, "status must not be null");
    }

    public static WriteOutcome written(Transaction transaction) {
        return new WriteOutcome(transaction, Status.WRITTEN, null);
    }

    public static WriteOutcome failed(Transaction transaction, String reason) {
        return new WriteOutcome(transaction, Status.FAILED, reason);
    }
}
