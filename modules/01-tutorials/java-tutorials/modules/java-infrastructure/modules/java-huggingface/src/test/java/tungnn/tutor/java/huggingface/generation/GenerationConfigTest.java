package tungnn.tutor.java.huggingface.generation;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tungnn.tutor.java.huggingface.config.HfConfig;

class GenerationConfigTest {

  @Test
  void readsMarianStyleConfig(@TempDir Path dir) throws IOException {
    Files.writeString(
        dir.resolve("config.json"),
        """
        {"pad_token_id": 65000, "decoder_start_token_id": 65000, "eos_token_id": 0,
         "max_length": 512, "bad_words_ids": [[65000]]}
        """);

    var cfg = GenerationConfig.load(dir, HfConfig.fromEnv(name -> null).withMaxNewTokens(128));

    assertThat(cfg.padTokenId()).isEqualTo(65000);
    assertThat(cfg.decoderStartTokenId()).isEqualTo(65000);
    assertThat(cfg.eosTokenId()).isZero();
    assertThat(cfg.maxNewTokens()).isEqualTo(128); // capped by HfConfig
    assertThat(cfg.bannedTokenIds().get(65000)).isTrue();
  }

  @Test
  void generationConfigOverridesAndEosMayBeArray(@TempDir Path dir) throws IOException {
    Files.writeString(dir.resolve("config.json"), "{\"eos_token_id\": 2, \"max_length\": 20}");
    Files.writeString(dir.resolve("generation_config.json"), "{\"eos_token_id\": [5, 6]}");

    var cfg = GenerationConfig.load(dir, HfConfig.fromEnv(name -> null));

    assertThat(cfg.eosTokenId()).isEqualTo(5);
    assertThat(cfg.maxNewTokens()).isEqualTo(20);
  }
}
