package tungnn.tutor.java.tool.finance.core.state;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tools.jackson.databind.json.JsonMapper;
import tungnn.tutor.java.jackson.JsonMapperUtils;
import tungnn.tutor.java.jackson.JsonUtils;
import tungnn.tutor.java.tool.finance.core.model.Transaction;
import tungnn.tutor.java.tool.finance.core.source.SyncCursor;

public final class FileSyncStateStore implements SyncStateStore {

    private record StateFile(Map<String, String> cursors) {}

    private final Path directory;
    private final Path stateFile;
    private final JsonMapper mapper = JsonMapperUtils.createJsonMapper();
    private final Map<String, String> cursors = new HashMap<>();
    private final Map<String, List<Transaction>> ledgers = new HashMap<>();

    public FileSyncStateStore(Path directory) {
        this.directory = directory;
        this.stateFile = directory.resolve("state.json");
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot create state directory " + directory, e);
        }
        if (Files.exists(stateFile)) {
            var loaded = JsonUtils.readFromFile(mapper, stateFile, StateFile.class);
            if (loaded != null && loaded.cursors() != null) {
                cursors.putAll(loaded.cursors());
            }
        }
    }

    @Override
    public SyncCursor cursor(String sourceId) {
        var value = cursors.get(sourceId);
        return value == null ? SyncCursor.initial() : new SyncCursor(Instant.parse(value));
    }

    @Override
    public void saveCursor(String sourceId, SyncCursor cursor) {
        cursors.put(sourceId, cursor.lastSyncedAt().toString());
        JsonUtils.writeToFile(mapper, stateFile, new StateFile(new HashMap<>(cursors)));
    }

    @Override
    public List<Transaction> ledger(String sinkId) {
        return List.copyOf(loadLedger(sinkId));
    }

    @Override
    public void recordWritten(String sinkId, Transaction transaction) {
        var line = JsonUtils.toJson(mapper, transaction) + System.lineSeparator();
        try {
            Files.writeString(
                    ledgerFile(sinkId),
                    line,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot append to the write ledger of " + sinkId, e);
        }
        loadLedger(sinkId).add(transaction);
    }

    private List<Transaction> loadLedger(String sinkId) {
        return ledgers.computeIfAbsent(sinkId, id -> {
            var file = ledgerFile(id);
            var loaded = new ArrayList<Transaction>();
            if (Files.exists(file)) {
                try {
                    for (var line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                        if (!line.isBlank()) {
                            loaded.add(JsonUtils.fromJson(mapper, line, Transaction.class));
                        }
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException("Cannot read the write ledger of " + id, e);
                }
            }
            return loaded;
        });
    }

    private Path ledgerFile(String sinkId) {
        return directory.resolve("ledger-" + sinkId + ".jsonl");
    }
}
