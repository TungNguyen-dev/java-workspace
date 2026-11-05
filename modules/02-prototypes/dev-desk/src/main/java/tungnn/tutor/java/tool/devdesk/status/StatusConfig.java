package tungnn.tutor.java.tool.devdesk.status;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tungnn.tutor.java.selenium.driver.options.ChromeOptionUtil;
import tungnn.tutor.java.tool.devdesk.status.Checks.Requirement;

@Configuration
@EnableConfigurationProperties(DeskProperties.class)
class StatusConfig {

  @Bean
  SystemStatusService systemStatusService(DeskProperties properties) {
    var status = properties.status();
    var checks = new ArrayList<StatusCheck>();

    checks.add(Checks.jvm());
    checks.add(
        Checks.path(
            "Máy",
            "Thư mục lưu trữ",
            Path.of(status.storageDir()),
            Requirement.OPTIONAL,
            "Sẽ được tạo khi có công cụ ghi file."));

    checks.add(
        Checks.path(
            "Trình duyệt tự động",
            "ChromeDriver",
            Path.of(envOrDefault(ChromeOptionUtil.DRIVER_PATH_ENV, ChromeOptionUtil.DEFAULT_DRIVER_PATH)),
            Requirement.OPTIONAL,
            "Công cụ thu thập web sẽ không chạy được."));
    checks.add(
        Checks.path(
            "Trình duyệt tự động",
            "Chrome for Testing",
            Path.of(envOrDefault(ChromeOptionUtil.BINARY_PATH_ENV, ChromeOptionUtil.DEFAULT_BINARY_PATH)),
            Requirement.OPTIONAL,
            "Công cụ thu thập web sẽ không chạy được."));

    checks.add(
        Checks.env(
            "Khoá và cấu hình",
            "Gemini",
            "GEMINI_API_KEY",
            Requirement.OPTIONAL,
            "Dịch bằng Gemini sẽ không chạy."));
    checks.add(
        Checks.env(
            "Khoá và cấu hình",
            "Hugging Face",
            "HF_TOKEN",
            Requirement.OPTIONAL,
            "Dịch bằng Hugging Face sẽ không chạy."));
    checks.add(
        Checks.env(
            "Khoá và cấu hình",
            "Tesseract",
            "TESSDATA_PATH",
            Requirement.OPTIONAL,
            "Nhận dạng chữ trong ảnh sẽ không chạy."));

    checks.add(
        Checks.tcp(
            "Dịch vụ cục bộ",
            "Ollama",
            status.ollama(),
            Requirement.OPTIONAL,
            "Dịch bằng mô hình cục bộ sẽ không chạy."));
    checks.add(
        Checks.tcp(
            "Dịch vụ cục bộ",
            "Kafka",
            status.kafka(),
            Requirement.OPTIONAL,
            "Các demo Kafka sẽ không chạy."));

    return new SystemStatusService(List.copyOf(checks));
  }

  private static String envOrDefault(String name, String fallback) {
    var value = System.getenv(name);
    return value == null || value.isBlank() ? fallback : value;
  }
}
