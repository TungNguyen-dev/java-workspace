package tungnn.tutor.java.tool.finance.core.extract;

import java.util.List;
import tungnn.tutor.java.tool.finance.core.model.Transaction;

public record ExtractionResult(List<Transaction> transactions, List<String> issues) {

    public ExtractionResult {
        transactions = List.copyOf(transactions);
        issues = List.copyOf(issues);
    }

    public static ExtractionResult of(List<Transaction> transactions) {
        return new ExtractionResult(transactions, List.of());
    }
}
