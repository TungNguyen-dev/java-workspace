package tungnn.tutor.java.jackson.v3;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

public final class ObjectMapperCreator {

    private ObjectMapperCreator() {}

    public static ObjectMapper createDefault() {
        return JsonUtils.defaultBuilder().build();
    }

    public static ObjectMapper createPretty() {
        return JsonUtils.defaultBuilder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();
    }

    public static ObjectMapper createStrict() {
        return JsonMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }

    public static ObjectMapper createSnakeCase() {
        return JsonUtils.defaultBuilder()
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .build();
    }
}
