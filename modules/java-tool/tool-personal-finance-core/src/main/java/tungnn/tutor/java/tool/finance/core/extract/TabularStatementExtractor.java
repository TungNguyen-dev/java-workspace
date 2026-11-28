package tungnn.tutor.java.tool.finance.core.extract;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.csv.CSVFormat;
import tungnn.tutor.java.csv.CsvUtils;
import tungnn.tutor.java.infra.document.adapter.office.excel.ExcelDocumentUtils;
import tungnn.tutor.java.tool.finance.config.StatementLayout;
import tungnn.tutor.java.tool.finance.core.model.Direction;
import tungnn.tutor.java.tool.finance.core.model.Money;
import tungnn.tutor.java.tool.finance.core.model.RawDocument;
import tungnn.tutor.java.tool.finance.core.model.Transaction;
import tungnn.tutor.java.tool.finance.utils.AmountUtils;
import tungnn.tutor.java.tool.finance.utils.MediaTypes;

public final class TabularStatementExtractor implements TransactionExtractor {

    private static final ZoneId ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    public record Binding(StatementLayout layout, String accountId) {}

    private final Map<String, Binding> bindingBySource;

    public TabularStatementExtractor(Map<String, Binding> bindingBySource) {
        this.bindingBySource = Map.copyOf(bindingBySource);
    }

    @Override
    public boolean supports(RawDocument document) {
        return MediaTypes.isTabular(document.mediaType()) && bindingBySource.containsKey(document.sourceId());
    }

    @Override
    public ExtractionResult extract(RawDocument document) {
        var binding = bindingBySource.get(document.sourceId());
        var rows = readRows(document);
        var layout = binding.layout();
        var formatter = DateTimeFormatter.ofPattern(layout.dateFormat());
        var transactions = new ArrayList<Transaction>();
        var issues = new ArrayList<String>();
        for (var i = layout.skipRows(); i < rows.size(); i++) {
            var row = rows.get(i);
            var date = parseDate(cell(row, layout.dateColumn()), formatter);
            if (date.isEmpty()) {
                continue;
            }
            var signed = signedAmount(row, layout);
            if (signed.isEmpty() || signed.get().signum() == 0) {
                issues.add("Row %d of %s has no usable amount".formatted(i + 1, document.origin()));
                continue;
            }
            var amount = signed.get();
            var direction = amount.signum() < 0 ? Direction.DEBIT : Direction.CREDIT;
            var reference = layout.referenceColumn() == null ? null : text(cell(row, layout.referenceColumn()));
            transactions.add(new Transaction(
                    UUID.nameUUIDFromBytes((document.sourceId() + "|" + document.origin() + "|" + i).getBytes())
                            .toString(),
                    binding.accountId(),
                    date.get(),
                    new Money(amount.abs(), Money.VND),
                    direction,
                    text(cell(row, layout.descriptionColumn())),
                    reference,
                    null,
                    document.kind(),
                    document.sourceId()));
        }
        return new ExtractionResult(transactions, issues);
    }

    private static Optional<BigDecimal> signedAmount(List<Object> row, StatementLayout layout) {
        if (layout.amountColumn() != null) {
            return toAmount(cell(row, layout.amountColumn()));
        }
        var debit = layout.debitColumn() == null ? Optional.<BigDecimal>empty() : toAmount(cell(row, layout.debitColumn()));
        if (debit.isPresent() && debit.get().signum() != 0) {
            return Optional.of(debit.get().abs().negate());
        }
        return layout.creditColumn() == null
                ? Optional.empty()
                : toAmount(cell(row, layout.creditColumn())).map(BigDecimal::abs);
    }

    private static List<List<Object>> readRows(RawDocument document) {
        var path = Path.of(document.origin());
        try {
            if (MediaTypes.CSV.equals(document.mediaType())) {
                try (var stream = CsvUtils.readAsStream(path, record -> new ArrayList<Object>(record.toList()), CSVFormat.DEFAULT)) {
                    return stream.map(r -> (List<Object>) r).toList();
                }
            }
            try (var workbook = ExcelDocumentUtils.readWorkbook(path)) {
                var rows = new ArrayList<List<Object>>();
                for (var row : workbook.getSheetAt(0)) {
                    var cells = new ArrayList<Object>();
                    for (var c = 0; c < Math.max(row.getLastCellNum(), 0); c++) {
                        cells.add(ExcelDocumentUtils.getCellContent(row.getCell(c)));
                    }
                    rows.add(cells);
                }
                return rows;
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read " + path, e);
        }
    }

    private static Object cell(List<Object> row, int index) {
        return index >= 0 && index < row.size() ? row.get(index) : null;
    }

    private static String text(Object value) {
        return value == null ? null : String.valueOf(value).strip();
    }

    private static Optional<LocalDate> parseDate(Object value, DateTimeFormatter formatter) {
        if (value instanceof Date date) {
            return Optional.of(date.toInstant().atZone(ZONE).toLocalDate());
        }
        var raw = text(value);
        if (raw == null || raw.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(LocalDate.parse(raw, formatter));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    private static Optional<BigDecimal> toAmount(Object value) {
        if (value instanceof Number number) {
            return Optional.of(BigDecimal.valueOf(number.doubleValue()));
        }
        return Optional.ofNullable(value).flatMap(v -> AmountUtils.parse(String.valueOf(v)));
    }
}
