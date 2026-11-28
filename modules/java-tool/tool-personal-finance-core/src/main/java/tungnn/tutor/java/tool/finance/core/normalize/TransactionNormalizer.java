package tungnn.tutor.java.tool.finance.core.normalize;

import tungnn.tutor.java.tool.finance.core.model.Money;
import tungnn.tutor.java.tool.finance.core.model.Transaction;

public final class TransactionNormalizer {

    public Transaction normalize(Transaction tx) {
        var description = tx.description() == null ? null : tx.description().strip().replaceAll("\\s+", " ");
        var reference = tx.reference() == null || tx.reference().isBlank() ? null : tx.reference().strip();
        var money = new Money(tx.money().amount(), tx.money().currency().strip().toUpperCase());
        return new Transaction(
                tx.id(),
                tx.accountId(),
                tx.bookedOn(),
                money,
                tx.direction(),
                description,
                reference,
                tx.category(),
                tx.sourceKind(),
                tx.sourceId());
    }
}
