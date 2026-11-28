package tungnn.tutor.java.tool.finance.core.model;

import java.time.LocalDate;
import java.util.Objects;

public record Transaction(
        String id,
        String accountId,
        LocalDate bookedOn,
        Money money,
        Direction direction,
        String description,
        String reference,
        String category,
        SourceKind sourceKind,
        String sourceId) {

    public Transaction {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(accountId, "accountId must not be null");
        Objects.requireNonNull(bookedOn, "bookedOn must not be null");
        Objects.requireNonNull(money, "money must not be null");
        Objects.requireNonNull(direction, "direction must not be null");
        Objects.requireNonNull(sourceKind, "sourceKind must not be null");
        Objects.requireNonNull(sourceId, "sourceId must not be null");
    }

    public Transaction withCategory(String newCategory) {
        return new Transaction(
                id, accountId, bookedOn, money, direction, description, reference, newCategory, sourceKind, sourceId);
    }
}
