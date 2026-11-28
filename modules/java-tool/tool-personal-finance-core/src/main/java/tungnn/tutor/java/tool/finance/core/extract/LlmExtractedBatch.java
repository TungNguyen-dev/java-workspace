package tungnn.tutor.java.tool.finance.core.extract;

import java.math.BigDecimal;
import java.util.List;

public record LlmExtractedBatch(
        List<Item> transactions, BigDecimal openingBalance, BigDecimal closingBalance) {

    public record Item(String date, BigDecimal amount, String direction, String description, String reference) {}
}
