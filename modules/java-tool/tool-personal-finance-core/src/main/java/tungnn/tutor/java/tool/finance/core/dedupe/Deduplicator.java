package tungnn.tutor.java.tool.finance.core.dedupe;

import java.util.ArrayList;
import java.util.List;
import tungnn.tutor.java.tool.finance.core.model.Transaction;

public final class Deduplicator {

    public record Result(List<Transaction> kept, int duplicates) {}

    private static final class Slot {
        private Transaction tx;
        private boolean paired;

        private Slot(Transaction tx) {
            this.tx = tx;
        }
    }

    public Result deduplicate(List<Transaction> incoming, List<Transaction> alreadyWritten) {
        var ledgerPool = new ArrayList<>(alreadyWritten);
        var slots = new ArrayList<Slot>();
        var duplicates = 0;
        for (var tx : incoming) {
            var ledgerHit = indexOf(ledgerPool, tx);
            if (ledgerHit >= 0) {
                // TODO: when the written copy came from an email and tx is a statement row, upgrade it in the target app.
                ledgerPool.remove(ledgerHit);
                duplicates++;
                continue;
            }
            var slot = slots.stream()
                    .filter(s -> !s.paired && TransactionMatcher.matches(s.tx, tx))
                    .findFirst();
            if (slot.isEmpty()) {
                slots.add(new Slot(tx));
                continue;
            }
            duplicates++;
            var existing = slot.get();
            existing.paired = true;
            var incomingWins = SourcePriority.rank(tx.sourceKind()) > SourcePriority.rank(existing.tx.sourceKind());
            existing.tx = incomingWins ? merge(tx, existing.tx) : merge(existing.tx, tx);
        }
        return new Result(slots.stream().map(s -> s.tx).toList(), duplicates);
    }

    private static int indexOf(List<Transaction> pool, Transaction tx) {
        for (var i = 0; i < pool.size(); i++) {
            if (TransactionMatcher.matches(pool.get(i), tx)) {
                return i;
            }
        }
        return -1;
    }

    private static Transaction merge(Transaction winner, Transaction loser) {
        return new Transaction(
                winner.id(),
                winner.accountId(),
                winner.bookedOn(),
                winner.money(),
                winner.direction(),
                winner.description(),
                winner.reference() != null ? winner.reference() : loser.reference(),
                winner.category() != null ? winner.category() : loser.category(),
                winner.sourceKind(),
                winner.sourceId());
    }
}
