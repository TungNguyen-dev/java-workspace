package tungnn.tutor.java.jackson;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public final class JsonUtils {

    private JsonUtils() {}

    public static String toJson(JsonMapper mapper, Object object) {
        Objects.requireNonNull(mapper, "mapper must not be null");
        Objects.requireNonNull(object, "object must not be null");

        try {
            return mapper.writeValueAsString(object);
        } catch (JacksonException e) {
            throw new JsonException("Failed to serialize " + object.getClass().getName() + " to JSON", e);
        }
    }

    public static <T> T fromJson(JsonMapper mapper, String json, Class<T> clazz) {
        Objects.requireNonNull(mapper, "mapper must not be null");
        Objects.requireNonNull(json, "json must not be null");
        Objects.requireNonNull(clazz, "clazz must not be null");

        return parse(mapper, mapper.constructType(clazz), json);
    }

    public static <T> T fromJson(JsonMapper mapper, String json, TypeReference<T> typeRef) {
        Objects.requireNonNull(mapper, "mapper must not be null");
        Objects.requireNonNull(json, "json must not be null");
        Objects.requireNonNull(typeRef, "typeRef must not be null");

        return parse(mapper, mapper.constructType(typeRef), json);
    }

    public static void writeToFile(JsonMapper mapper, Path path, Object object) {
        Objects.requireNonNull(mapper, "mapper must not be null");
        Objects.requireNonNull(path, "path must not be null");
        Objects.requireNonNull(object, "object must not be null");

        Path parent = path.toAbsolutePath().getParent();
        Path temp = null;
        try {
            Files.createDirectories(parent);
            temp = Files.createTempFile(parent, path.getFileName().toString(), ".tmp");
            try (OutputStream out = Files.newOutputStream(temp)) {
                mapper.writeValue(out, object);
            }
            try {
                Files.move(temp, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | JacksonException e) {
            throw new JsonException("Failed to write JSON to file: " + path, e);
        } finally {
            if (temp != null) {
                try {
                    Files.deleteIfExists(temp);
                } catch (IOException ignored) {
                    // best-effort cleanup of the temp file after a failed write
                }
            }
        }
    }

    public static <T> T readFromFile(JsonMapper mapper, Path path, Class<T> clazz) {
        Objects.requireNonNull(mapper, "mapper must not be null");
        Objects.requireNonNull(path, "path must not be null");
        Objects.requireNonNull(clazz, "clazz must not be null");

        return readFile(mapper, mapper.constructType(clazz), path);
    }

    public static <T> T readFromFile(JsonMapper mapper, Path path, TypeReference<T> typeRef) {
        Objects.requireNonNull(mapper, "mapper must not be null");
        Objects.requireNonNull(path, "path must not be null");
        Objects.requireNonNull(typeRef, "typeRef must not be null");

        return readFile(mapper, mapper.constructType(typeRef), path);
    }

    public static void writeToStream(JsonMapper mapper, OutputStream os, Object object) {
        Objects.requireNonNull(mapper, "mapper must not be null");
        Objects.requireNonNull(os, "outputStream must not be null");
        Objects.requireNonNull(object, "object must not be null");

        try {
            mapper.writeValue(os, object);
        } catch (JacksonException e) {
            throw new JsonException("Failed to write " + object.getClass().getName() + " to stream", e);
        }
    }

    public static <T> T readFromStream(JsonMapper mapper, InputStream is, Class<T> clazz) {
        Objects.requireNonNull(mapper, "mapper must not be null");
        Objects.requireNonNull(is, "inputStream must not be null");
        Objects.requireNonNull(clazz, "clazz must not be null");

        return readStream(mapper, mapper.constructType(clazz), is);
    }

    public static <T> T readFromStream(JsonMapper mapper, InputStream is, TypeReference<T> typeRef) {
        Objects.requireNonNull(mapper, "mapper must not be null");
        Objects.requireNonNull(is, "inputStream must not be null");
        Objects.requireNonNull(typeRef, "typeRef must not be null");

        return readStream(mapper, mapper.constructType(typeRef), is);
    }

    private static <T> T parse(JsonMapper mapper, JavaType type, String json) {
        try {
            return mapper.readValue(json, type);
        } catch (JacksonException e) {
            throw new JsonException("Failed to deserialize JSON to " + type, e);
        }
    }

    private static <T> T readFile(JsonMapper mapper, JavaType type, Path path) {
        try (InputStream in = Files.newInputStream(path)) {
            return mapper.readValue(in, type);
        } catch (IOException | JacksonException e) {
            throw new JsonException("Failed to read JSON from file " + path + " as " + type, e);
        }
    }

    private static <T> T readStream(JsonMapper mapper, JavaType type, InputStream is) {
        try {
            return mapper.readValue(is, type);
        } catch (JacksonException e) {
            throw new JsonException("Failed to read JSON from stream as " + type, e);
        }
    }
}
