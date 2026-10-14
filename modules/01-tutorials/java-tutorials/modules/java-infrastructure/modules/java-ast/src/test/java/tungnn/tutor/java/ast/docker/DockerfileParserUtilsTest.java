package tungnn.tutor.java.ast.docker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.jimschubert.docker.ast.CmdInstruction;
import com.github.jimschubert.docker.ast.DockerInstruction;
import com.github.jimschubert.docker.ast.EnvInstruction;
import com.github.jimschubert.docker.ast.FromInstruction;
import com.github.jimschubert.docker.ast.RunInstruction;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tungnn.tutor.java.ast.docker.DockerfileParserUtils.DockerInstructionType;

class DockerfileParserUtilsTest {

  @Test
  @DisplayName(
      "Utility class constructor should throw AssertionError when instantiated via reflection")
  void utilityClassConstructorTest() throws NoSuchMethodException {
    Constructor<DockerfileParserUtils> constructor =
        DockerfileParserUtils.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    assertThatThrownBy(constructor::newInstance)
        .isInstanceOf(InvocationTargetException.class)
        .hasCauseInstanceOf(AssertionError.class)
        .hasRootCauseMessage("Utility class must not be instantiated");
  }

  // ---------------------------------------------------------------- DockerInstructionType

  @Nested
  @DisplayName("DockerInstructionType Tests")
  class DockerInstructionTypeTests {

    @Test
    @DisplayName("of() should return Optional.empty when instruction is null")
    void of_NullInstruction_ReturnsEmpty() {
      assertThat(DockerInstructionType.of(null)).isEmpty();
    }

    @Test
    @DisplayName("of() should map known DockerInstruction to corresponding DockerInstructionType")
    void of_KnownInstruction_ReturnsMatchingType() {
      FromInstruction fromMock = mock(FromInstruction.class);
      EnvInstruction envMock = mock(EnvInstruction.class);

      assertThat(DockerInstructionType.of(fromMock)).contains(DockerInstructionType.FROM);
      assertThat(DockerInstructionType.of(envMock)).contains(DockerInstructionType.ENV);
    }

    @Test
    @DisplayName("of() should return Optional.empty for unmapped DockerInstruction implementation")
    void of_UnknownInstruction_ReturnsEmpty() {
      DockerInstruction customInstructionMock = mock(DockerInstruction.class);
      assertThat(DockerInstructionType.of(customInstructionMock)).isEmpty();
    }
  }

  // ---------------------------------------------------------------- Parse Dockerfile

  @Nested
  @DisplayName("parseDockerfile Tests")
  class ParseDockerfileTests {

    @Test
    @DisplayName("parseDockerfile(InputStream) should throw NPE when stream is null")
    void parseInputStream_NullStream_ThrowsNPE() {
      assertThatThrownBy(() -> DockerfileParserUtils.parseDockerfile((InputStream) null))
          .isInstanceOf(NullPointerException.class)
          .hasMessage("dockerfile stream must not be null");
    }

    @Test
    @DisplayName("parseDockerfile(InputStream) should successfully parse valid Dockerfile content")
    void parseInputStream_ValidContent_ReturnsInstructions() throws IOException {
      String dockerfileContent =
          """
          FROM openjdk:17-slim
          ENV APP_PORT=8080
          """;

      try (InputStream inputStream =
          new ByteArrayInputStream(dockerfileContent.getBytes(StandardCharsets.UTF_8))) {
        List<DockerInstruction> instructions = DockerfileParserUtils.parseDockerfile(inputStream);

        assertThat(instructions).hasSize(2);
        assertThat(instructions.get(0)).isInstanceOf(FromInstruction.class);
        assertThat(instructions.get(1)).isInstanceOf(EnvInstruction.class);
      }
    }

    @Test
    @DisplayName(
        "parseDockerfile(InputStream) should throw UncheckedIOException when stream fails on read")
    void parseInputStream_IOException_ThrowsUncheckedIOException() throws IOException {
      try (InputStream faultyStream = mock(InputStream.class)) {
        when(faultyStream.readAllBytes()).thenThrow(new IOException("Stream error"));
        when(faultyStream.read()).thenThrow(new IOException("Stream error"));
        when(faultyStream.read(org.mockito.ArgumentMatchers.any()))
            .thenThrow(new IOException("Stream error"));
        when(faultyStream.read(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.anyInt(),
                org.mockito.ArgumentMatchers.anyInt()))
            .thenThrow(new IOException("Stream error"));

        assertThatThrownBy(() -> DockerfileParserUtils.parseDockerfile(faultyStream))
            .isInstanceOf(UncheckedIOException.class)
            .hasMessageContaining("Failed to read Dockerfile stream");
      }
    }

    @Test
    @DisplayName("parseDockerfile(Path) should throw NPE when path is null")
    void parsePath_NullPath_ThrowsNPE() {
      assertThatThrownBy(() -> DockerfileParserUtils.parseDockerfile((Path) null))
          .isInstanceOf(NullPointerException.class)
          .hasMessage("dockerfile path must not be null");
    }

    @Test
    @DisplayName(
        "parseDockerfile(Path) should throw IllegalArgumentException when file does not exist or is directory")
    void parsePath_NonRegularFile_ThrowsIllegalArgumentException(@TempDir Path tempDir) {
      Path nonExistentPath = tempDir.resolve("Dockerfile.nonexistent");

      assertThatThrownBy(() -> DockerfileParserUtils.parseDockerfile(nonExistentPath))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Not a readable Dockerfile");

      assertThatThrownBy(() -> DockerfileParserUtils.parseDockerfile(tempDir))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Not a readable Dockerfile");
    }

    @Test
    @DisplayName("parseDockerfile(Path) should successfully read and parse file")
    void parsePath_ValidFile_ReturnsInstructions(@TempDir Path tempDir) throws IOException {
      Path dockerfilePath = tempDir.resolve("Dockerfile");
      String content =
          """
          FROM alpine:latest
          RUN echo 'Hello World'
          """;
      Files.writeString(dockerfilePath, content);

      List<DockerInstruction> instructions = DockerfileParserUtils.parseDockerfile(dockerfilePath);

      assertThat(instructions).hasSize(2);
      assertThat(instructions.get(0)).isInstanceOf(FromInstruction.class);
      assertThat(instructions.get(1)).isInstanceOf(RunInstruction.class);
    }
  }

  // ---------------------------------------------------------------- Filter Instructions

  @Nested
  @DisplayName("filterInstructionByType Tests")
  class FilterInstructionByTypeTests {

    @Test
    @DisplayName("filterInstructionByType should throw NPE when type is null")
    void filter_NullType_ThrowsNPE() {
      assertThatThrownBy(() -> DockerfileParserUtils.filterInstructionByType(List.of(), null))
          .isInstanceOf(NullPointerException.class)
          .hasMessage("type must not be null");
    }

    @Test
    @DisplayName(
        "filterInstructionByType should return empty list when instructions list is null or empty")
    void filter_NullOrEmptyInstructions_ReturnsEmptyList() {
      assertThat(DockerfileParserUtils.filterInstructionByType(null, DockerInstructionType.FROM))
          .isEmpty();
      assertThat(
              DockerfileParserUtils.filterInstructionByType(List.of(), DockerInstructionType.FROM))
          .isEmpty();
    }

    @Test
    @DisplayName(
        "filterInstructionByType should filter instructions correctly matching specified type")
    void filter_ValidInstructions_ReturnsOnlyMatchingTypes() {
      FromInstruction fromInstruction = mock(FromInstruction.class);
      EnvInstruction envInstruction1 = mock(EnvInstruction.class);
      EnvInstruction envInstruction2 = mock(EnvInstruction.class);
      RunInstruction runInstruction = mock(RunInstruction.class);

      List<DockerInstruction> instructions =
          List.of(fromInstruction, envInstruction1, runInstruction, envInstruction2);

      List<DockerInstruction> filteredEnvs =
          DockerfileParserUtils.filterInstructionByType(instructions, DockerInstructionType.ENV);

      assertThat(filteredEnvs).hasSize(2).containsExactly(envInstruction1, envInstruction2);
    }
  }

  // ---------------------------------------------------------------- Write & Render Dockerfile

  @Nested
  @DisplayName("Render & Write Dockerfile Tests")
  class RenderAndWriteDockerfileTests {

    @Test
    @DisplayName("renderDockerfileContent should return empty string when input is null or empty")
    void render_NullOrEmptyInput_ReturnsEmptyString() {
      assertThat(DockerfileParserUtils.renderDockerfileContent(null)).isEmpty();
      assertThat(DockerfileParserUtils.renderDockerfileContent(List.of())).isEmpty();
    }

    @Test
    @DisplayName("renderDockerfileContent should skip null or blank instructions")
    void render_NullOrBlankInstructions_Skipped() {
      DockerInstruction blankMock = mock(DockerInstruction.class);
      when(blankMock.toCanonicalForm()).thenReturn("   ");

      assertThat(DockerfileParserUtils.renderDockerfileContent(List.of(blankMock))).isEmpty();
    }

    @Test
    @DisplayName(
        "renderDockerfileContent should insert extra blank line between different instruction types")
    void render_DifferentInstructionTypes_InsertsExtraNewline() {
      FromInstruction fromMock = mock(FromInstruction.class);
      when(fromMock.toCanonicalForm()).thenReturn("FROM alpine:latest");

      EnvInstruction envMock1 = mock(EnvInstruction.class);
      when(envMock1.toCanonicalForm()).thenReturn("ENV PORT=8080");

      EnvInstruction envMock2 = mock(EnvInstruction.class);
      when(envMock2.toCanonicalForm()).thenReturn("ENV MODE=prod");

      CmdInstruction cmdMock = mock(CmdInstruction.class);
      when(cmdMock.toCanonicalForm()).thenReturn("CMD [\"app\"]");

      List<DockerInstruction> instructions = List.of(fromMock, envMock1, envMock2, cmdMock);

      String expected =
          """
          FROM alpine:latest

          ENV PORT=8080
          ENV MODE=prod

          CMD ["app"]
          """
              .replace("\n", System.lineSeparator());

      String rendered = DockerfileParserUtils.renderDockerfileContent(instructions);

      assertThat(rendered).isEqualTo(expected);
    }

    @Test
    @DisplayName("writeDockerfile should throw NPE when path is null")
    void write_NullPath_ThrowsNPE() {
      assertThatThrownBy(() -> DockerfileParserUtils.writeDockerfile(null, List.of()))
          .isInstanceOf(NullPointerException.class)
          .hasMessage("dockerfile path must not be null");
    }

    @Test
    @DisplayName(
        "writeDockerfile should create parent directories and write rendered content to file")
    void write_ValidFile_WritesCorrectly(@TempDir Path tempDir) throws IOException {
      Path nestedFilePath = tempDir.resolve("subfolder").resolve("Dockerfile");

      FromInstruction fromMock = mock(FromInstruction.class);
      when(fromMock.toCanonicalForm()).thenReturn("FROM alpine:3.18");

      DockerfileParserUtils.writeDockerfile(nestedFilePath, List.of(fromMock));

      assertThat(Files.exists(nestedFilePath)).isTrue();
      String fileContent = Files.readString(nestedFilePath, StandardCharsets.UTF_8);
      assertThat(fileContent).isEqualTo("FROM alpine:3.18" + System.lineSeparator());
    }
  }
}
