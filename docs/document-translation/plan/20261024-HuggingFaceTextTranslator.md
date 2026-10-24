# Plan điều chỉnh: `HuggingFaceTextTranslator` trong module core, dùng Spring AI

Tài liệu tham khảo và plan trước của mình khác nhau ở một điểm chính: cách gọi API. Vì core đã có Spring AI, mình đổi hướng so với plan trước (khi đó mình đề xuất `HttpClient` thuần).

## 1. Quyết định chính

| Phương án | Ưu | Nhược |
|---|---|---|
| **A. Spring AI `ChatClient` (khuyến nghị)** | Giống hệt `GeminiApiTextTranslator`; không tự parse JSON; dễ test bằng mock `ChatClient` | Cần thêm 1 dependency và tự dựng client để không đụng auto-config |
| B. `HttpClient` thuần (như tài liệu tham khảo) | Không thêm dependency | Tự lo request/response, retry, parse |

Chọn A, nhưng **không** thêm `spring-ai-starter-model-openai`. Starter sẽ tự tạo thêm `ChatModel`, xung đột với `ChatClient.Builder` đang auto-config từ Google GenAI. Thay vào đó:
- Thêm dependency `org.springframework.ai:spring-ai-openai` (không phải starter) vào `document-translation-tool-core/pom.xml`. Version đã được `spring-ai-bom` 1.1.8 quản lý.
- Tự dựng `OpenAiChatModel` trỏ tới `https://router.huggingface.co/v1`, không qua auto-config. Cách này phù hợp nguyên tắc "Hugging Face chỉ là chi tiết infrastructure", và Gemini vẫn chạy song song không bị ảnh hưởng.

Cần xác minh khi code: `OpenAiApi.builder()` mặc định nối thêm `/v1/chat/completions`. Có thể phải đặt `baseUrl = https://router.huggingface.co` hoặc dùng `completionsPath("/chat/completions")` kèm base có `/v1`, tùy chữ ký builder ở 1.1.8.

## 2. Cấu trúc trong core

```text
core/infrastructure/translator/text/
├── GeminiApiTextTranslator.java        (đã có)
├── HuggingFaceTextTranslator.java      (mới)
└── huggingface/
    ├── HuggingFaceProperties.java      (record: baseUrl, token, model, temperature, maxTokens, timeout)
    └── HuggingFaceChatClientFactory.java (dựng ChatClient từ properties)
```

- `HuggingFaceTextTranslator extends AbstractTextTranslator`, nhận `ChatClient` qua constructor, y hệt `GeminiApiTextTranslator`. Class này không biết gì về HTTP hay token.
- `HuggingFaceChatClientFactory` gom toàn bộ chi tiết Hugging Face (base URL, token, model, temperature = 0, timeout) và trả về `ChatClient`. Nhờ vậy việc đổi model chỉ là đổi config.
- Domain, `AbstractDocumentTranslator` và 3 translator tài liệu giữ nguyên, như tài liệu tham khảo yêu cầu.

## 3. Prompt

POC dùng lại `prompt-trans-v2.md` làm system prompt và gửi `TextTranslationRequest` dạng JSON làm user message, đúng như `GeminiApiTextTranslator`. Lý do: prompt này đã có rule giữ placeholder/format và rule chống prompt injection, nên hai provider có hành vi nhất quán để so sánh. Prompt gọn hơn trong tài liệu tham khảo là phương án dự phòng nếu model nhỏ làm theo prompt v2 kém.

Hậu xử lý output (trong translator): bỏ khối `<think>...</think>`, bỏ code fence, `trim`. Nếu kết quả rỗng trong khi nguồn không rỗng thì ném exception để `AbstractTextTranslator` trả `Failure`.

## 4. Cấu hình

```yaml
huggingface:
  base-url: https://router.huggingface.co/v1
  token: ${HF_TOKEN}
  model: <model-id>
  temperature: 0
  timeout-seconds: 120
```

Properties này nên nằm ở module app (`@ConfigurationProperties`). Core chỉ nhận record `HuggingFaceProperties`, giữ core thuần thư viện. Đặt chọn provider trong `AppConfig` theo `doctrans.translator.provider` (`gemini` mặc định | `huggingface`) bằng `@ConditionalOnProperty`.

## 5. Các bước thực hiện

**Phase 1: POC**
1. Thêm `spring-ai-openai` vào pom core, build thử để chắc không xung đột với Google GenAI.
2. Viết `HuggingFaceProperties` + `HuggingFaceChatClientFactory`.
3. Viết `HuggingFaceTextTranslator` + hậu xử lý output.
4. Unit test (mock `ChatClient`): thành công, ném lỗi thành `Failure`, có `<think>`, kết quả rỗng.
5. Wiring trong `AppConfig` với `@ConditionalOnProperty`.
6. Smoke test thật với `本品用于治疗高血压。` sang tiếng Việt, gắn `@EnabledIfEnvironmentVariable("HF_TOKEN")`.

**Phase 2: Production hardening**
- Giới hạn concurrency: `ConcurrentUtils.executeConcurrently` hiện chạy theo số unique block, nên cần `Semaphore` hoặc giới hạn pool để tránh HTTP 429.
- Retry với backoff cho 429, 5xx, timeout; không retry 401/400.
- Mã lỗi có cấu trúc thay cho `Error("", message)` hiện tại.
- Log không để lộ token hay toàn bộ nội dung tài liệu.
- Fallback model, glossary/medical prompt, kiểm tra chất lượng.

## 6. Lưu ý

- **Translation memory dùng chung**: key hiện chỉ gồm `(language, sourceText)`, nên bản dịch của Gemini và Hugging Face lẫn vào nhau. Khi so sánh chất lượng, nên tách thư mục memory theo provider.
- **Model**: nên thử vài model trên cùng một tập câu Trung/Nhật/Anh sang Việt trước khi chốt mặc định. Model reasoning thường chậm và hay in phần suy luận.
- **Quota**: kiểm tra giới hạn free tier với file Excel lớn, vì mỗi unique cell là một request.

Nếu bạn đồng ý, mình sẽ viết code cho các bước 1–3 trước. Bạn có model ID mặc định nào muốn dùng không?