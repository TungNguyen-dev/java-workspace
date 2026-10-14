package tungnn.tutor.java.ini.ini4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Optional;
import org.ini4j.Ini;

public final class IniUtil {

  private IniUtil() {}

  /* ===================== READ ===================== */

  public static Ini read(Path path) {
    try {
      if (!Files.exists(path)) {
        return new Ini(); // empty ini
      }
      return new Ini(path.toFile());
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to read INI file: " + path, e);
    }
  }

  public static Optional<String> get(Path path, String section, String key) {
    Ini ini = read(path);
    Ini.Section sec = ini.get(section);
    if (sec == null) return Optional.empty();
    return Optional.ofNullable(sec.get(key));
  }

  /* ===================== WRITE ===================== */

  public static void write(Path path, Map<String, Map<String, String>> data) {
    Ini ini = new Ini();

    data.forEach(
        (section, kv) -> {
          Ini.Section sec = ini.add(section);
          sec.putAll(kv);
        });

    safeSave(ini, path);
  }

  /* ===================== APPEND / UPDATE ===================== */

  public static void append(Path path, String section, Map<String, String> values) {
    Ini ini = read(path);

    Ini.Section sec = ini.get(section);
    if (sec == null) {
      sec = ini.add(section);
    }

    sec.putAll(values);

    safeSave(ini, path);
  }

  public static void put(Path path, String section, String key, String value) {
    append(path, section, Map.of(key, value));
  }

  public static void removeKey(Path path, String section, String key) {
    Ini ini = read(path);

    Ini.Section sec = ini.get(section);
    if (sec != null) {
      sec.remove(key);
    }

    safeSave(ini, path);
  }

  public static void removeSection(Path path, String section) {
    Ini ini = read(path);
    ini.remove(section);
    safeSave(ini, path);
  }

  /* ===================== INTERNAL ===================== */

  private static void safeSave(Ini ini, Path path) {
    try {
      Path tmp = path.resolveSibling(path.getFileName() + ".tmp");

      ini.store(tmp.toFile());

      Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);

    } catch (IOException e) {
      throw new UncheckedIOException("Failed to save INI file: " + path, e);
    }
  }
}
