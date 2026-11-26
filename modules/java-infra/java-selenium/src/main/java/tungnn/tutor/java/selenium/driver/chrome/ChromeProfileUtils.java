package tungnn.tutor.java.selenium.driver.chrome;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.openqa.selenium.json.Json;

public final class ChromeProfileUtils {

    private static final System.Logger LOGGER = System.getLogger(ChromeProfileUtils.class.getName());

    private static final int RESTORE_ON_STARTUP_NEW_TAB_PAGE = 5;
    private static final List<String> LEGACY_SESSION_FILES =
            List.of("Current Session", "Current Tabs", "Last Session", "Last Tabs");

    private ChromeProfileUtils() {
        throw new UnsupportedOperationException("Cannot instantiate a utility class");
    }

    public static void resetSessionState(Path profilePath) {
        Objects.requireNonNull(profilePath, "profilePath must not be null");
        var defaultDir = profilePath.resolve("Default");

        deleteQuietly(defaultDir.resolve("Sessions"));
        LEGACY_SESSION_FILES.forEach(name -> deleteQuietly(defaultDir.resolve(name)));
        resetPreferences(defaultDir.resolve("Preferences"));
    }

    @SuppressWarnings("unchecked")
    private static void resetPreferences(Path preferencesFile) {
        if (!Files.isRegularFile(preferencesFile)) {
            return;
        }
        try {
            var json = new Json();
            var preferences = new LinkedHashMap<String, Object>(
                    json.toType(Files.readString(preferencesFile, StandardCharsets.UTF_8), Json.MAP_TYPE));

            var profile = childMap(preferences, "profile");
            profile.put("exit_type", "Normal");
            profile.put("exited_cleanly", true);

            var session = childMap(preferences, "session");
            session.put("restore_on_startup", RESTORE_ON_STARTUP_NEW_TAB_PAGE);
            session.remove("startup_urls");

            Files.writeString(preferencesFile, json.toJson(preferences), StandardCharsets.UTF_8);
        } catch (IOException | RuntimeException e) {
            LOGGER.log(System.Logger.Level.WARNING, "Unable to reset session preferences in " + preferencesFile, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> childMap(Map<String, Object> parent, String key) {
        var child = new LinkedHashMap<String, Object>();
        if (parent.get(key) instanceof Map<?, ?> existing) {
            child.putAll((Map<String, Object>) existing);
        }
        parent.put(key, child);
        return child;
    }

    private static void deleteQuietly(Path path) {
        if (!Files.exists(path)) {
            return;
        }
        try (var paths = Files.walk(path)) {
            for (var entry : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(entry);
            }
        } catch (IOException | RuntimeException e) {
            LOGGER.log(System.Logger.Level.WARNING, "Unable to delete " + path, e);
        }
    }
}
