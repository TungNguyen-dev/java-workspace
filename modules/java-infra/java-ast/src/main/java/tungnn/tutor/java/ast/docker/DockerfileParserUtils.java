package tungnn.tutor.java.ast.docker;

import com.github.jimschubert.docker.ast.*;
import com.github.jimschubert.docker.ast.DockerInstruction;
import com.github.jimschubert.docker.parser.DockerfileParser;
import com.github.jimschubert.docker.parser.ParserError;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class DockerfileParserUtils {

  private static final DockerfileParser DOCKERFILE_PARSER = new DockerfileParser();

  private DockerfileParserUtils() {
    throw new AssertionError("Utility class must not be instantiated");
  }

  public static List<DockerInstruction> parseDockerfile(Path dockerfile) {
    Objects.requireNonNull(dockerfile, "dockerfile path must not be null");
    if (!Files.isRegularFile(dockerfile)) {
      throw new IllegalArgumentException(
          "Not a readable Dockerfile: " + dockerfile.toAbsolutePath());
    }
    try (InputStream in = Files.newInputStream(dockerfile)) {
      return parseDockerfile(in);
    } catch (IOException e) {
      throw new UncheckedIOException(
          "Failed to read Dockerfile: " + dockerfile.toAbsolutePath(), e);
    } catch (IllegalStateException e) {
      throw new IllegalStateException(
          "Failed to parse Dockerfile: " + dockerfile.toAbsolutePath(), e);
    }
  }

  // ---------------------------------------------------------------- parse

  public static List<DockerInstruction> parseDockerfile(InputStream dockerfile) {
    Objects.requireNonNull(dockerfile, "dockerfile stream must not be null");
    try {
      return DOCKERFILE_PARSER.parseDockerfile(dockerfile);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to read Dockerfile stream", e);
    } catch (ParserError e) {
      throw new IllegalStateException("Invalid Dockerfile content", e);
    }
  }

  public static List<DockerInstruction> filterInstructionByType(
      List<DockerInstruction> instructions, DockerInstructionType type) {
    Objects.requireNonNull(type, "type must not be null");
    if (instructions == null || instructions.isEmpty()) {
      return List.of();
    }
    return instructions.stream().filter(type.instructionClass::isInstance).toList();
  }

  // ----------------------------------------------------------------- find

  public static void writeDockerfile(Path dockerfile, List<DockerInstruction> instructions) {
    Objects.requireNonNull(dockerfile, "dockerfile path must not be null");
    String content = renderDockerfileContent(instructions);
    try {
      Path parent = dockerfile.toAbsolutePath().getParent();
      if (parent != null) {
        Files.createDirectories(parent);
      }
      Files.writeString(dockerfile, content, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException(
          "Failed to write Dockerfile: " + dockerfile.toAbsolutePath(), e);
    }
  }

  // ---------------------------------------------------------------- write

  public static String renderDockerfileContent(List<DockerInstruction> instructions) {
    if (instructions == null || instructions.isEmpty()) {
      return "";
    }

    final String NEWLINE = System.lineSeparator();
    StringBuilder builder = new StringBuilder();
    DockerInstructionType previousType = null;
    boolean isFirstInstruction = true;

    for (DockerInstruction instruction : instructions) {
      if (instruction == null) {
        continue;
      }

      String canonicalForm = instruction.toCanonicalForm();
      if (canonicalForm == null || canonicalForm.isBlank()) {
        continue;
      }

      DockerInstructionType currentType = DockerInstructionType.of(instruction).orElse(null);

      if (!isFirstInstruction) {
        // Insert an extra blank line when switching to a different instruction type
        if (!Objects.equals(currentType, previousType)) {
          builder.append(NEWLINE);
        }
        builder.append(NEWLINE);
      }

      builder.append(canonicalForm.strip());
      previousType = currentType;
      isFirstInstruction = false;
    }

    if (builder.isEmpty()) {
      return "";
    }

    builder.append(NEWLINE);
    return builder.toString();
  }

  public enum DockerInstructionType {
    // 1. Base Image
    FROM(FromInstruction.class),

    // 2. Environment & Working Directory
    ENV(EnvInstruction.class),
    WORKDIR(WorkdirInstruction.class),

    // 3. Build & Dependencies Installation
    RUN(RunInstruction.class),

    // 4. Source & Asset Copying
    COPY(CopyInstruction.class),
    ADD(AddInstruction.class),

    // 5. Container Runtime Configuration
    EXPOSE(ExposeInstruction.class),
    VOLUME(VolumeInstruction.class),

    // 6. Execution Instructions (placed at the end of Dockerfile)
    ENTRYPOINT(EntrypointInstruction.class),
    CMD(CmdInstruction.class);

    private final Class<? extends DockerInstruction> instructionClass;

    DockerInstructionType(Class<? extends DockerInstruction> instructionClass) {
      this.instructionClass = instructionClass;
    }

    public static Optional<DockerInstructionType> of(DockerInstruction instruction) {
      if (instruction == null) {
        return Optional.empty();
      }
      return Arrays.stream(values())
          .filter(type -> type.instructionClass.isInstance(instruction))
          .findFirst();
    }
  }
}
