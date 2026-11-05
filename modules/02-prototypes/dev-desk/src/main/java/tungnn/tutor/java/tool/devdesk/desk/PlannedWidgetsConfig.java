package tungnn.tutor.java.tool.devdesk.desk;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Các công cụ sẽ làm ở những giai đoạn sau. Xoá bean ở đây khi widget thật được thêm vào. */
@Configuration
class PlannedWidgetsConfig {

  @Bean
  DeskWidget plannedTranslation() {
    return new PlannedWidget(
        "translation",
        "Dịch tài liệu",
        "Dịch file docx, xlsx, pptx sang VI, JA, EN, ZH và tải bản dịch về.",
        TrayId.DOCUMENTS,
        10);
  }

  @Bean
  DeskWidget plannedOcr() {
    return new PlannedWidget(
        "ocr",
        "Nhận dạng chữ trong ảnh",
        "Đọc chữ từ ảnh chụp màn hình hoặc PDF scan bằng Tesseract.",
        TrayId.DOCUMENTS,
        20);
  }

  @Bean
  DeskWidget plannedCrawler() {
    return new PlannedWidget(
        "crawler",
        "Thu thập trang web",
        "Dán danh sách URL, lưu nội dung thành ghi chú Markdown và xem tiến độ từng trang.",
        TrayId.WEB_DATA,
        10);
  }

  @Bean
  DeskWidget plannedAws() {
    return new PlannedWidget(
        "aws",
        "AWS",
        "Xem danh sách role, assume role với MFA và chạy lệnh qua SSM.",
        TrayId.CLOUD,
        10);
  }

  @Bean
  DeskWidget plannedCron() {
    return new PlannedWidget(
        "cron",
        "Biểu thức cron",
        "Kiểm tra, mô tả và tính các lần chạy tiếp theo của biểu thức cron.",
        TrayId.CLOUD,
        20);
  }
}
