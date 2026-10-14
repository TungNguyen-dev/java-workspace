package tungnn.tutor.java.jackson.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.*;

public final class ObjectMapperCreator {

  private ObjectMapperCreator() {}

  public static ObjectMapper createDefault() {
    ObjectMapper mapper = new ObjectMapper();

    // Serialization
    mapper.setDefaultPropertyInclusion(JsonInclude.Include.ALWAYS);
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // Deserialization
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    return mapper;
  }

  public static ObjectMapper createPretty() {
    ObjectMapper mapper = createDefault();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    return mapper;
  }

  public static ObjectMapper createStrict() {
    ObjectMapper mapper = new ObjectMapper();

    mapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    return mapper;
  }

  public static ObjectMapper createSnakeCase() {
    ObjectMapper mapper = createDefault();

    mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    return mapper;
  }
}
