package tungnn.tutor.java.huggingface.hub;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import tungnn.tutor.java.huggingface.config.HfConfig;
import tungnn.tutor.java.huggingface.exception.HfException;
import tungnn.tutor.java.huggingface.exception.UnsupportedModelException;

/**
 * Resolves a {@link ModelRef} to a local directory, downloading the requested files from the Hub
 * when needed.
 *
 * <p>Cache layout (simplified, not symlink-based like huggingface_hub): {@code
 * <cacheDir>/models--org--name/snapshots/<revision>/<file>}.
 *
 * <p>The Authorization header is only sent to the configured endpoint host, never to CDN hosts
 * reached via redirects.
 */
public final class ModelRepository {

  private static final System.Logger LOG = System.getLogger(ModelRepository.class.getName());
  private static final int MAX_REDIRECTS = 5;

  private final HfConfig config;
  private final HttpClient http;
  private final ObjectMapper mapper = new ObjectMapper();

  public ModelRepository(HfConfig config) {
    this.config = config;
    this.http =
        HttpClient.newBuilder()
            .connectTimeout(config.connectTimeout())
            .followRedirects(HttpClient.Redirect.NEVER) // handled manually (see open())
            .build();
  }

  public Path snapshotDir(ModelRef ref) {
    return config
        .cacheDir()
        .resolve("models--" + ref.id().replace("/", "--"))
        .resolve("snapshots")
        .resolve(ref.revision().replace('/', '_'));
  }

  /** Returns the model directory containing at least all repo files accepted by {@code filter}. */
  public Path resolve(ModelRef ref, Predicate<String> filter) {
    if (ref.localDir() != null) {
      if (!Files.isDirectory(ref.localDir())) {
        throw new HfException("Model directory does not exist: " + ref.localDir());
      }
      return ref.localDir();
    }

    var snapshot = snapshotDir(ref);
    if (config.offline()) {
      if (!Files.isDirectory(snapshot)) {
        throw new HfException("Offline mode and model not cached: " + ref.displayName());
      }
      return snapshot;
    }

    List<String> files;
    try {
      files = listFiles(ref).stream().filter(filter).toList();
    } catch (HfException e) {
      if (Files.isDirectory(snapshot)) {
        LOG.log(System.Logger.Level.WARNING, "Hub unreachable, using cached snapshot: " + snapshot);
        return snapshot;
      }
      throw e;
    }

    if (files.isEmpty()) {
      throw new UnsupportedModelException(
          "No matching files in repo " + ref.displayName() + " (is there an ONNX export?)");
    }
    for (var file : files) {
      download(ref, snapshot, file);
    }
    return snapshot;
  }

  List<String> listFiles(ModelRef ref) {
    var uri = URI.create(config.endpoint() + "/api/models/" + ref.id() + "/revision/" + enc(ref.revision()));
    try (var in = open(uri).body()) {
      JsonNode siblings = mapper.readTree(in).path("siblings");
      var names = new ArrayList<String>();
      for (JsonNode sibling : siblings) {
        var name = sibling.path("rfilename").asText(null);
        if (name != null) {
          names.add(name);
        }
      }
      return names;
    } catch (IOException e) {
      throw new HfException("Cannot list files of " + ref.displayName(), e);
    }
  }

  private void download(ModelRef ref, Path snapshot, String fileName) {
    var root = snapshot.toAbsolutePath().normalize();
    var target = root.resolve(fileName).normalize();
    if (!target.startsWith(root)) {
      throw new HfException("Refusing to write outside the cache dir: " + fileName);
    }
    try {
      if (Files.isRegularFile(target) && Files.size(target) > 0) {
        return;
      }
      Files.createDirectories(target.getParent());
      var part = target.resolveSibling(target.getFileName() + ".part");

      var uri = URI.create(config.endpoint() + "/" + ref.id() + "/resolve/" + enc(ref.revision()) + "/" + encPath(fileName));
      LOG.log(System.Logger.Level.INFO, "Downloading " + ref.id() + "/" + fileName);
      try (InputStream in = open(uri).body()) {
        Files.copy(in, part, StandardCopyOption.REPLACE_EXISTING);
      }
      Files.move(part, target, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new HfException("Cannot download " + fileName + " of " + ref.displayName(), e);
    }
  }

  /** GET with manual redirect handling; throws {@link HfException} on non-200. */
  private HttpResponse<InputStream> open(URI uri) {
    var endpointHost = URI.create(config.endpoint()).getHost();
    var current = uri;
    try {
      for (int i = 0; i <= MAX_REDIRECTS; i++) {
        var request = HttpRequest.newBuilder(current).timeout(config.requestTimeout()).GET();
        if (config.token() != null && current.getHost().equalsIgnoreCase(endpointHost)) {
          request.header("Authorization", "Bearer " + config.token());
        }
        var response = http.send(request.build(), HttpResponse.BodyHandlers.ofInputStream());
        int status = response.statusCode();

        if (status >= 300 && status < 400) {
          var location =
              response.headers().firstValue("location").orElseThrow(() -> new HfException("Redirect without Location: " + uri));
          response.body().close();
          current = current.resolve(location);
          continue;
        }
        if (status == 200) {
          return response;
        }
        response.body().close();
        throw new HfException(describeError(status, uri));
      }
      throw new HfException("Too many redirects: " + uri);
    } catch (IOException e) {
      throw new HfException("HTTP error for " + uri, e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new HfException("Interrupted while contacting " + uri, e);
    }
  }

  private static String describeError(int status, URI uri) {
    return switch (status) {
      case 401, 403 -> "HTTP %d for %s: model is gated/private, set HF_TOKEN".formatted(status, uri);
      case 404 -> "HTTP 404 for %s: model, revision or file not found".formatted(uri);
      default -> "HTTP %d for %s".formatted(status, uri);
    };
  }

  private static String enc(String s) {
    return URLEncoder.encode(s, StandardCharsets.UTF_8).replace("+", "%20");
  }

  private static String encPath(String path) {
    return Arrays.stream(path.split("/")).map(ModelRepository::enc).collect(Collectors.joining("/"));
  }
}
