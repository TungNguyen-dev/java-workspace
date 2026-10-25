package tungnn.tutor.java.huggingface.hub;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Predicate;
import tungnn.tutor.java.huggingface.exception.HfException;
import tungnn.tutor.java.huggingface.exception.UnsupportedModelException;

/** Helpers to select which repo files to download and to locate them inside a model directory. */
public final class ModelFiles {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private static final Set<String> METADATA =
      Set.of(
          "config.json",
          "generation_config.json",
          "tokenizer.json",
          "tokenizer_config.json",
          "special_tokens_map.json");

  private ModelFiles() {}

  /**
   * Predicate accepting the metadata files plus the given ONNX files (at repo root or under {@code
   * onnx/}), including their external-data companions ({@code <name>_data}).
   */
  public static Predicate<String> metadataAnd(String... onnxFileNames) {
    return name -> {
      if (METADATA.contains(name)) {
        return true;
      }
      for (String onnx : onnxFileNames) {
        if (name.equals(onnx)
            || name.equals("onnx/" + onnx)
            || name.equals(onnx + "_data")
            || name.equals("onnx/" + onnx + "_data")) {
          return true;
        }
      }
      return false;
    };
  }

  /** Finds {@code <dir>/onnx/<name>} or {@code <dir>/<name>}. */
  public static Path findOnnx(Path dir, String name) {
    var inSubDir = dir.resolve("onnx").resolve(name);
    if (Files.isRegularFile(inSubDir)) {
      return inSubDir;
    }
    var atRoot = dir.resolve(name);
    if (Files.isRegularFile(atRoot)) {
      return atRoot;
    }
    throw new UnsupportedModelException(
        "Missing ONNX file '%s' in %s. Export the model first, e.g. "
                .formatted(name, dir)
            + "`optimum-cli export onnx --model <id> --task <task> <out_dir>`, "
            + "or use a repo that already ships an onnx/ folder.");
  }

  /** Reads a JSON file in the model dir; returns a missing node when the file is absent. */
  public static JsonNode readJson(Path dir, String fileName) {
    var file = dir.resolve(fileName);
    if (!Files.isRegularFile(file)) {
      return MissingNode.getInstance();
    }
    try {
      return MAPPER.readTree(file.toFile());
    } catch (IOException e) {
      throw new HfException("Cannot read " + file, e);
    }
  }

  public static Path requireFile(Path dir, String fileName) {
    var file = dir.resolve(fileName);
    if (!Files.isRegularFile(file)) {
      throw new UnsupportedModelException("Missing required file '" + fileName + "' in " + dir);
    }
    return file;
  }
}
