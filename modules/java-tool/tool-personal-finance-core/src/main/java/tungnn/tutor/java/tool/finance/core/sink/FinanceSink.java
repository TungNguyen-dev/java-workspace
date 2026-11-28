package tungnn.tutor.java.tool.finance.core.sink;

import java.util.List;
import java.util.function.Consumer;
import tungnn.tutor.java.tool.finance.core.model.Account;
import tungnn.tutor.java.tool.finance.core.model.Transaction;

public interface FinanceSink {

    String id();

    void upsertAccounts(List<Account> accounts);

    void writeTransactions(List<Transaction> transactions, Consumer<WriteOutcome> onEach);
}
