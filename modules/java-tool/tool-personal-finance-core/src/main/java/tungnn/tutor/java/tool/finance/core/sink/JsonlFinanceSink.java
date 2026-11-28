package tungnn.tutor.java.tool.finance.core.sink;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import tools.jackson.databind.json.JsonMapper;
import tungnn.tutor.java.jackson.JsonMapperUtils;
import tungnn.tutor.java.jackson.JsonUtils;
import tungnn.tutor.java.tool.finance.core.model.Account;
import tungnn.tutor.java.tool.finance.core.model.Transaction;

public final class JsonlFinanceSink implements FinanceSink {

    private final String id;
    private final Path transactionsFile;
    private final Path accountsFile;
    private final JsonMapper mapper = JsonMapperUtils.createJsonMapper();

    public JsonlFinanceSink(String id, Path directory) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(directory, "directory must not be null");
        this.transactionsFile = directory.resolve("transactions.jsonl");
        this.accountsFile = directory.resolve("accounts.json");
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot create sink directory " + directory, e);
        }
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public void upsertAccounts(List<Account> accounts) {
        JsonUtils.writeToFile(mapper, accountsFile, accounts);
    }

    @Override
    public void writeTransactions(List<Transaction> transactions, Consumer<WriteOutcome> onEach) {
        for (var transaction : transactions) {
            try {
                var line = JsonUtils.toJson(mapper, transaction) + System.lineSeparator();
                Files.writeString(
                        transactionsFile,
                        line,
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND);
                onEach.accept(WriteOutcome.written(transaction));
            } catch (IOException | RuntimeException e) {
                onEach.accept(WriteOutcome.failed(transaction, e.getMessage()));
            }
        }
    }
}
