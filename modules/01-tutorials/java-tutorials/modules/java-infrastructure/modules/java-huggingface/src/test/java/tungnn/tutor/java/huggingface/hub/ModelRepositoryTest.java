package tungnn.tutor.java.huggingface.hub;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tungnn.tutor.java.huggingface.config.HfConfig;
import tungnn.tutor.java.huggingface.exception.HfException;

class ModelRepositoryTest {

  private static HfConfig config(Path cache, boolean offline) {
    return HfConfig.fromEnv(name -> null).withCacheDir(cache).withOffline(offline);
  }

  @Test
  void localDirIsReturnedAsIs(@TempDir Path dir) {
    var repo = new ModelRepository(config(dir, true));
    assertThat(repo.resolve(ModelRef.local(dir), name -> true)).isEqualTo(dir);
  }

  @Test
  void offlineUsesCachedSnapshot(@TempDir Path cache) throws IOException {
    var ref = ModelRef.of("org/name");
    var repo = new ModelRepository(config(cache, true));
    Files.createDirectories(repo.snapshotDir(ref));

    assertThat(repo.resolve(ref, name -> true)).isEqualTo(repo.snapshotDir(ref));
  }

  @Test
  void offlineWithoutCacheFails(@TempDir Path cache) {
    var repo = new ModelRepository(config(cache, true));
    assertThatThrownBy(() -> repo.resolve(ModelRef.of("org/name"), name -> true))
        .isInstanceOf(HfException.class)
        .hasMessageContaining("Offline");
  }
}
