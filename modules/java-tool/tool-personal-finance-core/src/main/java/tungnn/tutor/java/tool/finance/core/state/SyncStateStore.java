package tungnn.tutor.java.tool.finance.core.state;

import java.util.List;
import tungnn.tutor.java.tool.finance.core.model.Transaction;
import tungnn.tutor.java.tool.finance.core.source.SyncCursor;

public interface SyncStateStore {

    SyncCursor cursor(String sourceId);

    void saveCursor(String sourceId, SyncCursor cursor);

    List<Transaction> ledger(String sinkId);

    void recordWritten(String sinkId, Transaction transaction);
}
