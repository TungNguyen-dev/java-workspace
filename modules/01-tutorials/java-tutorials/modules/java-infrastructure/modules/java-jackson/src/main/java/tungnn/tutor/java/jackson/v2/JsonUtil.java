package tungnn.tutor.java.jackson.v2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import tungnn.tutor.java.jackson.JsonException;

public final class JsonUtil {

  private JsonUtil() {}

  // =========================
  // String APIs
  // =========================

  public static String toJson(ObjectMapper mapper, Object object) {
    Objects.requireNonNull(mapper, "mapper must not be null");

    try {
      return mapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new JsonException("Failed to serialize object to JSON", e);
    }
  }

  public static <T> T fromJson(ObjectMapper mapper, String json, Class<T> clazz) {
    Objects.requireNonNull(mapper, "mapper must not be null");
    Objects.requireNonNull(json, "json must not be null");
    Objects.requireNonNull(clazz, "clazz must not be null");

    try {
      return mapper.readValue(json, clazz);
    } catch (JsonProcessingException e) {
      throw new JsonException("Failed to deserialize JSON", e);
    }
  }

  public static <T> T fromJson(ObjectMapper mapper, String json, TypeReference<T> typeRef) {
    Objects.requireNonNull(mapper, "mapper must not be null");
    Objects.requireNonNull(json, "json must not be null");
    Objects.requireNonNull(typeRef, "typeRef must not be null");

    try {
      return mapper.readValue(json, typeRef);
    } catch (JsonProcessingException e) {
      throw new JsonException("Failed to deserialize JSON (generic)", e);
    }
  }

  // =========================
  // File APIs
  // =========================

  public static void writeToFile(ObjectMapper mapper, Path path, Object object) {
    Objects.requireNonNull(mapper, "mapper must not be null");
    Objects.requireNonNull(path, "path must not be null");

    try {
      if (path.getParent() != null) {
        Files.createDirectories(path.getParent());
      }

      mapper.writeValue(path.toFile(), object);
    } catch (IOException e) {
      throw new JsonException("Failed to write JSON to file: " + path, e);
    }
  }

  public static <T> T readFromFile(ObjectMapper mapper, Path path, Class<T> clazz) {
    Objects.requireNonNull(mapper, "mapper must not be null");
    Objects.requireNonNull(path, "path must not be null");
    Objects.requireNonNull(clazz, "clazz must not be null");

    try {
      return mapper.readValue(path.toFile(), clazz);
    } catch (IOException e) {
      throw new JsonException("Failed to read JSON from file: " + path, e);
    }
  }

  public static <T> T readFromFile(ObjectMapper mapper, Path path, TypeReference<T> typeRef) {
    Objects.requireNonNull(mapper, "mapper must not be null");
    Objects.requireNonNull(path, "path must not be null");
    Objects.requireNonNull(typeRef, "typeRef must not be null");

    try {
      return mapper.readValue(path.toFile(), typeRef);
    } catch (IOException e) {
      throw new JsonException("Failed to read JSON from file: " + path, e);
    }
  }

  // =========================
  // Streaming APIs
  // =========================

  public static void writeToStream(ObjectMapper mapper, OutputStream os, Object object) {
    Objects.requireNonNull(mapper, "mapper must not be null");
    Objects.requireNonNull(os, "outputStream must not be null");

    try {
      mapper.writeValue(os, object);
    } catch (IOException e) {
      throw new JsonException("Failed to write JSON to stream", e);
    }
  }

  public static <T> T readFromStream(ObjectMapper mapper, InputStream is, Class<T> clazz) {
    Objects.requireNonNull(mapper, "mapper must not be null");
    Objects.requireNonNull(is, "inputStream must not be null");
    Objects.requireNonNull(clazz, "clazz must not be null");

    try {
      return mapper.readValue(is, clazz);
    } catch (IOException e) {
      throw new JsonException("Failed to read JSON from stream", e);
    }
  }

  public static <T> T readFromStream(
      ObjectMapper mapper, InputStream is, TypeReference<T> typeRef) {
    Objects.requireNonNull(mapper, "mapper must not be null");
    Objects.requireNonNull(is, "inputStream must not be null");
    Objects.requireNonNull(typeRef, "typeRef must not be null");

    try {
      return mapper.readValue(is, typeRef);
    } catch (IOException e) {
      throw new JsonException("Failed to read JSON from stream", e);
    }
  }
}
