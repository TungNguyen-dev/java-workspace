package tungnn.tutor.java.tool.finance.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import tungnn.tutor.java.tool.finance.core.model.Account;
import tungnn.tutor.java.tool.finance.core.model.Direction;
import tungnn.tutor.java.tool.finance.core.model.Money;
import tungnn.tutor.java.tool.finance.core.model.Transaction;
import tungnn.tutor.java.tool.finance.core.state.SyncStateStore;

public final class DailyNoteService {

    private static final String UNCATEGORIZED = "Uncategorized";

    private final SyncStateStore state;
    private final String sinkId;
    private final Path directory;
    private final Map<String, String> accountNames;

    public DailyNoteService(SyncStateStore state, String sinkId, Path directory, List<Account> accounts) {
        this.state = Objects.requireNonNull(state, "state must not be null");
        this.sinkId = Objects.requireNonNull(sinkId, "sinkId must not be null");
        this.directory = Objects.requireNonNull(directory, "directory must not be null");
        this.accountNames = accounts.stream().collect(Collectors.toMap(Account::id, Account::name, (a, b) -> a));
    }

    public Path write(LocalDate date) {
        var transactions = state.ledger(sinkId).stream()
                .filter(tx -> tx.bookedOn().equals(date))
                .sorted(Comparator.comparing(Transaction::accountId).thenComparing(Transaction::id))
                .toList();
        var file = directory.resolve(date + ".md");
        try {
            Files.createDirectories(directory);
            Files.writeString(file, render(date, transactions), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot write the daily note " + file, e);
        }
        return file;
    }

    String render(LocalDate date, List<Transaction> transactions) {
        var sb = new StringBuilder();
        sb.append("# Financial activity ").append(date).append(System.lineSeparator());
        sb.append(System.lineSeparator());
        if (transactions.isEmpty()) {
            return sb.append("No transactions recorded.")
                    .append(System.lineSeparator())
                    .toString();
        }

        var income = total(transactions, Direction.CREDIT);
        var expense = total(transactions, Direction.DEBIT);
        sb.append("- Income: ").append(format(income)).append(System.lineSeparator());
        sb.append("- Expense: ").append(format(expense)).append(System.lineSeparator());
        sb.append("- Net: ").append(format(income.subtract(expense))).append(System.lineSeparator());
        sb.append(System.lineSeparator());

        sb.append("## Transactions").append(System.lineSeparator()).append(System.lineSeparator());
        sb.append("| Account | Type | Amount | Description | Category |").append(System.lineSeparator());
        sb.append("| --- | --- | ---: | --- | --- |").append(System.lineSeparator());
        for (var tx : transactions) {
            sb.append("| ")
                    .append(cell(accountNames.getOrDefault(tx.accountId(), tx.accountId())))
                    .append(" | ")
                    .append(tx.direction() == Direction.CREDIT ? "Income" : "Expense")
                    .append(" | ")
                    .append(format(tx.money()))
                    .append(" | ")
                    .append(cell(tx.description()))
                    .append(" | ")
                    .append(cell(tx.category() == null ? UNCATEGORIZED : tx.category()))
                    .append(" |")
                    .append(System.lineSeparator());
        }

        var byCategory = new TreeMap<String, BigDecimal>();
        transactions.stream()
                .filter(tx -> tx.direction() == Direction.DEBIT)
                .forEach(tx -> byCategory.merge(
                        tx.category() == null ? UNCATEGORIZED : tx.category(),
                        tx.money().amount(),
                        BigDecimal::add));
        if (!byCategory.isEmpty()) {
            sb.append(System.lineSeparator()).append("## Expense by category").append(System.lineSeparator());
            sb.append(System.lineSeparator());
            byCategory.entrySet().stream()
                    .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                    .forEach(entry -> sb.append("- ")
                            .append(entry.getKey())
                            .append(": ")
                            .append(format(entry.getValue()))
                            .append(System.lineSeparator()));
        }
        return sb.toString();
    }

    private static BigDecimal total(List<Transaction> transactions, Direction direction) {
        return transactions.stream()
                .filter(tx -> tx.direction() == direction)
                .map(tx -> tx.money().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static String format(Money money) {
        var suffix = Money.VND.equals(money.currency()) ? "₫" : money.currency();
        return amount(money.amount()) + " " + suffix;
    }

    private static String format(BigDecimal amount) {
        return amount(amount) + " ₫";
    }

    private static String amount(BigDecimal value) {
        var symbols = new DecimalFormatSymbols(Locale.ROOT);
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        return new DecimalFormat("#,##0.##", symbols).format(value);
    }

    private static String cell(String value) {
        return value == null ? "" : value.replace("|", "\\|").replace("\n", " ");
    }
}
