package tungnn.tutor.java.huggingface.hub;

import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * Reference to a model: either a Hub repo ({@code org/name} + revision) or a local directory.
 *
 * <p>Repo id and revision are validated strictly because they are used to build cache paths.
 */
public record ModelRef(String id, String revision, Path localDir) {

  public static final String DEFAULT_REVISION = "main";

  private static final Pattern ID = Pattern.compile("[A-Za-z0-9._-]+(/[A-Za-z0-9._-]+)?");
  private static final Pattern REVISION = Pattern.compile("[A-Za-z0-9._-]+(/[A-Za-z0-9._-]+)*");

  public ModelRef {
    if (localDir == null) {
      if (id == null || !ID.matcher(id).matches() || id.contains("..")) {
        throw new IllegalArgumentException("Invalid model id: " + id);
      }
      revision = (revision == null || revision.isBlank()) ? DEFAULT_REVISION : revision;
      if (!REVISION.matcher(revision).matches() || revision.contains("..")) {
        throw new IllegalArgumentException("Invalid revision: " + revision);
      }
    }
  }

  public static ModelRef of(String id) {
    return new ModelRef(id, DEFAULT_REVISION, null);
  }

  public static ModelRef of(String id, String revision) {
    return new ModelRef(id, revision, null);
  }

  public static ModelRef local(Path dir) {
    var name = dir.toAbsolutePath().getFileName();
    return new ModelRef(name == null ? "local" : name.toString(), null, dir);
  }

  public String displayName() {
    return localDir != null ? localDir.toString() : id + "@" + revision;
  }
}
