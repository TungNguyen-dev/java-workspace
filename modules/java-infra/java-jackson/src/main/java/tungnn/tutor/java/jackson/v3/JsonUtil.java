package tungnn.tutor.java.jackson.v3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tungnn.tutor.java.jackson.JsonException;

public final class JsonUtil {

    private JsonUtil() {}

    public static JsonMapper createJsonMapper() {
        return defaultBuilder().build();
    }

    public static JsonMapper createJsonMapper(Consumer<JsonMapper.Builder> configurer) {
        JsonMapper.Builder builder = defaultBuilder();
        if (configurer != null) {
            configurer.accept(builder);
        }
        return builder.build();
    }

    public static JsonMapper.Builder defaultBuilder() {
        return JsonMapper.builder().findAndAddModules().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    // =========================
    // String APIs
    // =========================

    public static String toJson(ObjectMapper mapper, Object object) {
        Objects.requireNonNull(mapper, "mapper must not be null");

        try {
            return mapper.writeValueAsString(object);
        } catch (JacksonException e) {
            throw new JsonException("Failed to serialize object to JSON", e);
        }
    }

    public static <T> T fromJson(ObjectMapper mapper, String json, Class<T> clazz) {
        Objects.requireNonNull(mapper, "mapper must not be null");
        Objects.requireNonNull(json, "json must not be null");
        Objects.requireNonNull(clazz, "clazz must not be null");

        try {
            return mapper.readValue(json, clazz);
        } catch (JacksonException e) {
            throw new JsonException("Failed to deserialize JSON", e);
        }
    }

    public static <T> T fromJson(ObjectMapper mapper, String json, TypeReference<T> typeRef) {
        Objects.requireNonNull(mapper, "mapper must not be null");
        Objects.requireNonNull(json, "json must not be null");
        Objects.requireNonNull(typeRef, "typeRef must not be null");

        try {
            return mapper.readValue(json, typeRef);
        } catch (JacksonException e) {
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

            try (OutputStream out = Files.newOutputStream(path)) {
                mapper.writeValue(out, object);
            }
        } catch (IOException | JacksonException e) {
            throw new JsonException("Failed to write JSON to file: " + path, e);
        }
    }

    public static <T> T readFromFile(ObjectMapper mapper, Path path, Class<T> clazz) {
        Objects.requireNonNull(mapper, "mapper must not be null");
        Objects.requireNonNull(path, "path must not be null");
        Objects.requireNonNull(clazz, "clazz must not be null");

        try {
            try (InputStream in = Files.newInputStream(path)) {
                return mapper.readValue(in, clazz);
            }
        } catch (IOException | JacksonException e) {
            throw new JsonException("Failed to read JSON from file: " + path, e);
        }
    }

    public static <T> T readFromFile(ObjectMapper mapper, Path path, TypeReference<T> typeRef) {
        Objects.requireNonNull(mapper, "mapper must not be null");
        Objects.requireNonNull(path, "path must not be null");
        Objects.requireNonNull(typeRef, "typeRef must not be null");

        try {
            try (InputStream in = Files.newInputStream(path)) {
                return mapper.readValue(in, typeRef);
            }
        } catch (IOException | JacksonException e) {
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
        } catch (JacksonException e) {
            throw new JsonException("Failed to write JSON to stream", e);
        }
    }

    public static <T> T readFromStream(ObjectMapper mapper, InputStream is, Class<T> clazz) {
        Objects.requireNonNull(mapper, "mapper must not be null");
        Objects.requireNonNull(is, "inputStream must not be null");
        Objects.requireNonNull(clazz, "clazz must not be null");

        try {
            return mapper.readValue(is, clazz);
        } catch (JacksonException e) {
            throw new JsonException("Failed to read JSON from stream", e);
        }
    }

    public static <T> T readFromStream(ObjectMapper mapper, InputStream is, TypeReference<T> typeRef) {
        Objects.requireNonNull(mapper, "mapper must not be null");
        Objects.requireNonNull(is, "inputStream must not be null");
        Objects.requireNonNull(typeRef, "typeRef must not be null");

        try {
            return mapper.readValue(is, typeRef);
        } catch (JacksonException e) {
            throw new JsonException("Failed to read JSON from stream", e);
        }
    }
}
