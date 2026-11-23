package tungnn.tutor.java.selenium.driver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;
import org.openqa.selenium.WebDriver;

public interface WebDriverFactory {

  String DEFAULT_PROFILE_PREFIX = "profile_";

  private static List<WebDriver> joinAll(List<CompletableFuture<WebDriver>> futures) {
    List<WebDriver> created = new ArrayList<>(futures.size());
    RuntimeException failure = null;

    // All tasks are already running; joining each just collects results.
    // We keep going after a failure so we can clean up everything created.
    for (CompletableFuture<WebDriver> future : futures) {
      try {
        created.add(future.join());
      } catch (CompletionException | CancellationException e) {
        if (failure == null) {
          failure = e;
        }
      }
    }

    if (failure != null) {
      quitQuietly(created);
      throw failure;
    }
    return List.copyOf(created);
  }

  private static void quitQuietly(List<WebDriver> drivers) {
    for (WebDriver driver : drivers) {
      try {
        driver.quit();
      } catch (RuntimeException ignored) {
        // best-effort cleanup
      }
    }
  }

  /** Creates (or resolves) a WebDriver bound to the given profile. */
  WebDriver getWebDriver(String profileName);

  /**
   * Creates one WebDriver per profile concurrently, preserving input order. If any creation fails,
   * every driver already created in this call is quit before the error is propagated, so no browser
   * process is leaked.
   */
  default List<WebDriver> getWebDrivers(String... profileNames) {
    if (profileNames == null || profileNames.length == 0) {
      return List.of();
    }

    try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
      // Submit everything FIRST, then join — otherwise the stream would
      // supplyAsync+join each element in turn and run them serially.
      List<CompletableFuture<WebDriver>> futures =
          Arrays.stream(profileNames)
              .map(profile -> CompletableFuture.supplyAsync(() -> getWebDriver(profile), executor))
              .toList();

      return joinAll(futures);
    }
  }

  default List<WebDriver> getWebDrivers(int numberOfProfiles) {
    if (numberOfProfiles <= 0) {
      return List.of();
    }
    return getWebDrivers(
        IntStream.range(0, numberOfProfiles)
            .mapToObj(i -> DEFAULT_PROFILE_PREFIX + i)
            .toArray(String[]::new));
  }
}
