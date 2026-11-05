package tungnn.tutor.java.tool.devdesk.status;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "devdesk")
public record DeskProperties(@DefaultValue Status status) {

  /** Địa chỉ các dịch vụ cục bộ cần kiểm tra, dạng {@code host:port}. */
  public record Status(
      @DefaultValue("storage") String storageDir,
      @DefaultValue("localhost:11434") String ollama,
      @DefaultValue("localhost:9092") String kafka) {}
}
