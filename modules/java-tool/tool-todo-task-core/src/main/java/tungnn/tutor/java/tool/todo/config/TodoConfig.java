package tungnn.tutor.java.tool.todo.config;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public record TodoConfig(
        Path vaultDir, String inboxNote, String inboxHeading, List<String> excludes, String globalFilter) {

    public static final String VAULT_ENV = "TODO_VAULT_DIR";
    public static final String INBOX_NOTE_ENV = "TODO_INBOX_NOTE";
    public static final String INBOX_HEADING_ENV = "TODO_INBOX_HEADING";
    public static final String EXCLUDES_ENV = "TODO_EXCLUDES";
    public static final String GLOBAL_FILTER_ENV = "TODO_GLOBAL_FILTER";

    public static final String DEFAULT_INBOX_NOTE = "Inbox.md";

    public TodoConfig {
        vaultDir = vaultDir.toAbsolutePath().normalize();
        inboxNote = inboxNote == null || inboxNote.isBlank() ? DEFAULT_INBOX_NOTE : inboxNote;
        excludes = excludes == null ? List.of() : excludes;
    }

    public static TodoConfig fromEnv(Map<String, String> env, String vaultOverride) {
        var vault = vaultOverride != null ? vaultOverride : value(env, VAULT_ENV);
        if (vault == null) {
            throw new IllegalArgumentException("Vault directory is required: pass --vault or set " + VAULT_ENV);
        }
        var excludes = value(env, EXCLUDES_ENV);
        return new TodoConfig(
                Path.of(vault),
                value(env, INBOX_NOTE_ENV),
                value(env, INBOX_HEADING_ENV),
                excludes == null
                        ? List.of()
                        : Arrays.stream(excludes.split(","))
                                .map(String::strip)
                                .filter(s -> !s.isEmpty())
                                .toList(),
                value(env, GLOBAL_FILTER_ENV));
    }

    private static String value(Map<String, String> env, String key) {
        var raw = env.get(key);
        return raw == null || raw.isBlank() ? null : raw.strip();
    }
}
