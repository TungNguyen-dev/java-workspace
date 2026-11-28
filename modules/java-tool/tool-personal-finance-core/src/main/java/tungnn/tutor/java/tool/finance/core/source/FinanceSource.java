package tungnn.tutor.java.tool.finance.core.source;

import java.util.stream.Stream;
import tungnn.tutor.java.tool.finance.core.model.RawDocument;

public interface FinanceSource {

    String id();

    Stream<RawDocument> fetch(SyncCursor cursor);
}
