package tungnn.tutor.java.obsidian.config;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;

public record ObsidianConfig(
        Path executable, String vault, Duration timeout, String templatesFolder, String reportsFolder, String author) {

    public static final String CLI_PATH_ENV = "OBSIDIAN_CLI_PATH";
    public static final String VAULT_ENV = "OBSIDIAN_VAULT";
    public static final String TIMEOUT_SECONDS_ENV = "OBSIDIAN_TIMEOUT_SECONDS";
    public static final String TEMPLATES_FOLDER_ENV = "OBSIDIAN_TEMPLATES_FOLDER";
    public static final String REPORTS_FOLDER_ENV = "OBSIDIAN_REPORTS_FOLDER";
    public static final String AUTHOR_ENV = "OBSIDIAN_AUTHOR";

    public static final Path DEFAULT_EXECUTABLE = Path.of("/usr/local/bin/obsidian");
    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(60);
    public static final String DEFAULT_TEMPLATES_FOLDER = "Templates";
    public static final String DEFAULT_REPORTS_FOLDER = "Reports";

    public ObsidianConfig(Path executable, String vault, Duration timeout) {
        this(executable, vault, timeout, DEFAULT_TEMPLATES_FOLDER, DEFAULT_REPORTS_FOLDER, null);
    }

    public static ObsidianConfig forVault(String vault) {
        return new ObsidianConfig(DEFAULT_EXECUTABLE, vault, DEFAULT_TIMEOUT);
    }

    public static ObsidianConfig fromEnv() {
        return fromEnv(System.getenv());
    }

    public static ObsidianConfig fromEnv(Map<String, String> env) {
        return new ObsidianConfig(
                Path.of(value(env, CLI_PATH_ENV, DEFAULT_EXECUTABLE.toString())),
                value(env, VAULT_ENV, null),
                Duration.ofSeconds(seconds(env)),
                value(env, TEMPLATES_FOLDER_ENV, DEFAULT_TEMPLATES_FOLDER),
                value(env, REPORTS_FOLDER_ENV, DEFAULT_REPORTS_FOLDER),
                value(env, AUTHOR_ENV, null));
    }

    private static long seconds(Map<String, String> env) {
        var raw = value(env, TIMEOUT_SECONDS_ENV, null);
        if (raw == null) {
            return DEFAULT_TIMEOUT.toSeconds();
        }
        try {
            var parsed = Long.parseLong(raw);
            if (parsed <= 0) {
                throw new IllegalArgumentException(TIMEOUT_SECONDS_ENV + " must be positive: " + raw);
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(TIMEOUT_SECONDS_ENV + " must be a number: " + raw, e);
        }
    }

    private static String value(Map<String, String> env, String key, String fallback) {
        var raw = env.get(key);
        return raw == null || raw.isBlank() ? fallback : raw.strip();
    }
}
