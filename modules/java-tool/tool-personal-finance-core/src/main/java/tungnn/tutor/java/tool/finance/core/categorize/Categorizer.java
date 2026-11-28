package tungnn.tutor.java.tool.finance.core.categorize;

import java.util.Optional;
import tungnn.tutor.java.tool.finance.core.model.Transaction;

public interface Categorizer {

    Optional<String> categorize(Transaction transaction);
}
