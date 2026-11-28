package tungnn.tutor.java.tool.finance.config;

public record StatementLayout(
        String id,
        String format,
        int skipRows,
        int dateColumn,
        String dateFormat,
        int descriptionColumn,
        Integer referenceColumn,
        Integer amountColumn,
        Integer debitColumn,
        Integer creditColumn) {}
