package tungnn.tutor.java.huggingface.hub;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tungnn.tutor.java.huggingface.exception.UnsupportedModelException;

class ModelFilesTest {

  @Test
  void predicateSelectsMetadataAndRequestedOnnxOnly() {
    var filter = ModelFiles.metadataAnd("model.onnx");
    assertThat(filter.test("config.json")).isTrue();
    assertThat(filter.test("tokenizer.json")).isTrue();
    assertThat(filter.test("onnx/model.onnx")).isTrue();
    assertThat(filter.test("onnx/model.onnx_data")).isTrue();
    assertThat(filter.test("onnx/model_quantized.onnx")).isFalse();
    assertThat(filter.test("pytorch_model.bin")).isFalse();
  }

  @Test
  void findOnnxPrefersSubdirThenRoot(@TempDir Path dir) throws IOException {
    Files.createDirectories(dir.resolve("onnx"));
    Files.writeString(dir.resolve("onnx/model.onnx"), "x");
    assertThat(ModelFiles.findOnnx(dir, "model.onnx")).isEqualTo(dir.resolve("onnx/model.onnx"));
  }

  @Test
  void findOnnxExplainsHowToExport(@TempDir Path dir) {
    assertThatThrownBy(() -> ModelFiles.findOnnx(dir, "model.onnx"))
        .isInstanceOf(UnsupportedModelException.class)
        .hasMessageContaining("optimum-cli export onnx");
  }
}
