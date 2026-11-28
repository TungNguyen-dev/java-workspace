package tungnn.tutor.java.tool.finance;

import java.time.LocalDate;
import tungnn.tutor.java.tool.finance.core.model.Direction;
import tungnn.tutor.java.tool.finance.core.model.Money;
import tungnn.tutor.java.tool.finance.core.model.SourceKind;
import tungnn.tutor.java.tool.finance.core.model.Transaction;

public final class TestTransactions {

    private TestTransactions() {}

    public static Transaction debit(String id, SourceKind kind, String sourceId, LocalDate date, long amount, String ref) {
        return new Transaction(
                id, "acc-1", date, Money.vnd(amount), Direction.DEBIT, "desc " + id, ref, null, kind, sourceId);
    }
}
