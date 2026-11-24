package tungnn.tutor.java.jackson;

import java.util.function.Consumer;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

public final class JsonMapperUtils {

    private JsonMapperUtils() {}

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

    public static JsonMapper createPrettyMapper() {
        return defaultBuilder().enable(SerializationFeature.INDENT_OUTPUT).build();
    }

    public static JsonMapper createStrictMapper() {
        return defaultBuilder()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }

    public static JsonMapper createSnakeCaseMapper() {
        return defaultBuilder()
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .build();
    }

    private static JsonMapper.Builder defaultBuilder() {
        return JsonMapper.builder()
                .findAndAddModules()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(StreamReadFeature.AUTO_CLOSE_SOURCE)
                .disable(StreamWriteFeature.AUTO_CLOSE_TARGET);
    }
}
