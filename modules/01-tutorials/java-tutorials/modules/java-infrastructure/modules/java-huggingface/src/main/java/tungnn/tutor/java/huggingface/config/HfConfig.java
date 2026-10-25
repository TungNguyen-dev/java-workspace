package tungnn.tutor.java.huggingface.config;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Runtime configuration. Defaults follow the Hugging Face conventions: {@code HF_HOME}, {@code
 * HF_TOKEN} (or {@code HUGGINGFACE_HUB_TOKEN}), {@code HF_ENDPOINT}, {@code HF_HUB_OFFLINE}.
 *
 * @param cacheDir directory holding {@code models--org--name/snapshots/<revision>/...}
 * @param token Hub token, only needed for gated/private models (nullable)
 * @param intraOpThreads ONNX Runtime intra-op threads, 0 = ONNX Runtime default
 * @param maxNewTokens hard upper bound for generated tokens (seq2seq)
 */
public record HfConfig(
    Path cacheDir,
    String endpoint,
    String token,
    boolean offline,
    int intraOpThreads,
    int maxNewTokens,
    Duration connectTimeout,
    Duration requestTimeout) {

  public static final String DEFAULT_ENDPOINT = "https://huggingface.co";

  public HfConfig {
    Objects.requireNonNull(cacheDir, "cacheDir must not be null");
    Objects.requireNonNull(endpoint, "endpoint must not be null");
    Objects.requireNonNull(connectTimeout, "connectTimeout must not be null");
    Objects.requireNonNull(requestTimeout, "requestTimeout must not be null");
    if (maxNewTokens <= 0) {
      throw new IllegalArgumentException("maxNewTokens must be > 0");
    }
    if (intraOpThreads < 0) {
      throw new IllegalArgumentException("intraOpThreads must be >= 0");
    }
    endpoint = endpoint.endsWith("/") ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
    if (token != null && token.isBlank()) {
      token = null;
    }
  }

  public static HfConfig fromEnv() {
    return fromEnv(System::getenv);
  }

  public static HfConfig fromEnv(UnaryOperator<String> env) {
    var hfHome = env.apply("HF_HOME");
    var home =
        hfHome != null && !hfHome.isBlank()
            ? Path.of(hfHome)
            : Path.of(System.getProperty("user.home"), ".cache", "huggingface");

    return new HfConfig(
        home.resolve("hub"),
        firstNonBlank(env.apply("HF_ENDPOINT"), DEFAULT_ENDPOINT),
        firstNonBlank(env.apply("HF_TOKEN"), env.apply("HUGGINGFACE_HUB_TOKEN")),
        isTrue(env.apply("HF_HUB_OFFLINE")),
        0,
        256,
        Duration.ofSeconds(10),
        Duration.ofSeconds(60));
  }

  public HfConfig withCacheDir(Path newCacheDir) {
    return new HfConfig(
        newCacheDir, endpoint, token, offline, intraOpThreads, maxNewTokens, connectTimeout,
        requestTimeout);
  }

  public HfConfig withOffline(boolean newOffline) {
    return new HfConfig(
        cacheDir, endpoint, token, newOffline, intraOpThreads, maxNewTokens, connectTimeout,
        requestTimeout);
  }

  public HfConfig withMaxNewTokens(int newMaxNewTokens) {
    return new HfConfig(
        cacheDir, endpoint, token, offline, intraOpThreads, newMaxNewTokens, connectTimeout,
        requestTimeout);
  }

  private static String firstNonBlank(String a, String b) {
    return a != null && !a.isBlank() ? a : (b != null && !b.isBlank() ? b : null);
  }

  private static boolean isTrue(String value) {
    return value != null && (value.equals("1") || value.equalsIgnoreCase("true"));
  }
}
