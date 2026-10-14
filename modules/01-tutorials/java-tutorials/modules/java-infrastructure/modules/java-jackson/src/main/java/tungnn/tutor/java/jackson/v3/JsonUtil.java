package tungnn.tutor.java.jackson.v3;

import java.util.function.Consumer;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

public final class JsonUtil {

  private JsonUtil() {}

  /** Khởi tạo JsonMapper với cấu hình mặc định. */
  public static JsonMapper createJsonMapper() {
    return defaultBuilder().build();
  }

  /**
   * Khởi tạo JsonMapper cho phép tùy chỉnh Builder thông qua Consumer. Ví dụ:
   * JsonUtil.createJsonMapper(builder ->
   * builder.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
   */
  public static JsonMapper createJsonMapper(Consumer<JsonMapper.Builder> configurer) {
    JsonMapper.Builder builder = defaultBuilder();
    if (configurer != null) {
      configurer.accept(builder);
    }
    return builder.build();
  }

  /** Trả về Builder đã thiết lập các cấu hình mặc định chuẩn (thường dùng trong project). */
  public static JsonMapper.Builder defaultBuilder() {
    return JsonMapper.builder()
        .findAndAddModules()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
  }
}
