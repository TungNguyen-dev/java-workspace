package tungnn.tutor.java.selenium.driver.chrome;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ChromeProcessLauncher {

    private ChromeProcessLauncher() {
        throw new UnsupportedOperationException("Cannot instantiate a utility class");
    }

    public static Process launch(String profileName, List<String> urls) {
        return launch(profileName, urls, List.of());
    }

    public static Process launch(String profileName, List<String> urls, List<String> extraArgs) {
        Objects.requireNonNull(profileName, "profileName must not be null");
        Objects.requireNonNull(urls, "urls must not be null");
        Objects.requireNonNull(extraArgs, "extraArgs must not be null");

        var binary = System.getenv(ChromeDriverUtils.BINARY_PATH_ENV);
        if (binary == null || binary.isBlank()) {
            binary = ChromeDriverUtils.DEFAULT_BINARY_PATH;
        }
        var profilePath = ChromeDriverUtils.resolveProfilePath(profileName);
        ChromeProfileUtils.resetSessionState(profilePath);

        try {
            return new ProcessBuilder(buildCommand(binary, profilePath.toString(), extraArgs, urls))
                    .inheritIO()
                    .start();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to launch Chrome for profile " + profileName, e);
        }
    }

    static List<String> buildCommand(String binary, String profileDir, List<String> extraArgs, List<String> urls) {
        var command = new ArrayList<String>();
        command.add(binary);
        command.add("--user-data-dir=" + profileDir);
        command.add("--profile-directory=Default");
        command.add("--hide-crash-restore-bubble");
        command.add("--no-first-run");
        command.add("--no-default-browser-check");
        command.addAll(extraArgs);
        command.addAll(urls);
        return command;
    }
}
