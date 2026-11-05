# dev-desk

Bàn làm việc kỹ thuật: mỗi công cụ là một widget, xếp vào các khay theo nhóm dịch vụ.

## Chạy

```bash
mvn spring-boot:run
# mở http://localhost:8080 (chỉ bind 127.0.0.1)
```

Cần `java-infrastructure-starter` đã được `mvn install` trong workspace.

## Thêm một công cụ

1. Tạo bean implements `DeskWidget` (chọn `tray()`, `order()`, `endpoint()`).
2. Viết controller trả về fragment Thymeleaf tại `endpoint()`.
3. Xoá bean `PlannedWidget` tương ứng trong `PlannedWidgetsConfig`.

## Cấu trúc

- `desk/`: SPI `DeskWidget`, `TrayId`, `DeskCatalog`, trang chính.
- `status/`: widget Trạng thái hệ thống (check env, path, TCP, JVM).
- `templates/desk.html`, `templates/widgets/*.html`, `static/css|js`.

## Cấu hình (`application.yaml`)

`devdesk.status.storage-dir`, `ollama`, `kafka`; biến môi trường `OLLAMA_HOST_PORT`, `KAFKA_HOST_PORT`.
