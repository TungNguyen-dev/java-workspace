# Plan hoàn chỉnh: module `java-huggingface` (HF-local)

## 1. Mục tiêu, phạm vi, non-goals

**Mục tiêu:** một facade Java thuần, chạy model Hugging Face **cục bộ**, theo kiểu Python `pipeline(task, model)`:

```java
try (Pipeline<String, String> p =
        HfPipelines.load(Task.TRANSLATION, ModelRef.of("Helsinki-NLP/opus-mt-zh-vi"))) {
  String vi = p.run("你好，世界！");
}
```

| Trong phạm vi                                                                           | Ngoài phạm vi                                             |
|-----------------------------------------------------------------------------------------|-----------------------------------------------------------|
| Tải model từ Hub hoặc thư mục local, có cache                                           | Gọi API remote (Spring AI lo)                             |
| Tokenizer, chạy ONNX, pipeline theo task                                                | REST server / OpenAI-compatible                           |
| Task: embedding, classification, token-classification (NER), translation, summarization | Text generation LLM (dùng Ollama/llama.cpp qua Spring AI) |
| Lối đi tensor thô cho model chưa có pipeline                                            | Ảnh/audio (xét sau Phase 7)                               |
| Batch, pool, đóng tài nguyên an toàn                                                    | Fine-tuning/training                                      |

## 2. Prerequisites và system requirements

### 2.1 Môi trường build/chạy
| Hạng mục               | Yêu cầu                                                                                 | Ghi chú                                                                                                                                                 |
|------------------------|-----------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| JDK                    | **25** (đúng như `java-core`, `java-zip`)                                               | Cần chạy được `static void main()` instance-main như repo đang dùng                                                                                     |
| Maven                  | Bản đang dùng cho multi-module hiện tại                                                 | Module phải đứng được trong reactor `java-infrastructure`                                                                                               |
| Cờ JVM                 | `--enable-native-access=ALL-UNNAMED`                                                    | ONNX Runtime và tokenizer đều tải native lib; JDK mới cảnh báo/chặn nếu thiếu. Spike xác nhận cờ chính xác                                              |
| Hệ điều hành/kiến trúc | Cần **kiểm tra bạn đang dùng x64 hay arm64** (path Chrome cho thấy `mac-x64`)           | Native của ONNX Runtime và tokenizer phải có bản cho đúng OS/arch. Tôi không chắc mọi bản mới còn hỗ trợ macOS x64, nên **bắt buộc xác nhận ở Phase 0** |
| RAM                    | Tối thiểu ~4 GB trống cho model nhỏ (Marian ~300 MB trọng số); 8 GB+ nếu thêm model lớn | Đo thực tế ở spike                                                                                                                                      |
| Disk                   | 2–5 GB cho cache model và export                                                        | Model ONNX có thể lớn gấp vài lần bản nén                                                                                                               |
| GPU (tùy chọn)         | CUDA / CoreML                                                                           | Mặc định CPU, GPU là phase sau                                                                                                                          |

### 2.2 Mạng và tài khoản Hugging Face
- Truy cập `huggingface.co` để tải model lần đầu (proxy/firewall công ty có thể chặn; cần cấu hình proxy Java nếu có).
- **Token HF** (`HF_TOKEN`) chỉ cần cho model gated/private. Không bắt buộc cho model public.
- Chế độ **offline** sau khi đã cache (`HF_HUB_OFFLINE=1` hoặc cờ config).
- Biến môi trường: `HF_HOME` (thư mục cache, mặc định `~/.cache/huggingface`), `HF_TOKEN`.

### 2.3 Công cụ một lần để chuẩn bị model (không cần ở runtime)
- Python 3.10+ cùng `optimum[onnxruntime]` và `transformers` để export:
  ```
  optimum-cli export onnx --model Helsinki-NLP/opus-mt-zh-vi --task text2text-generation-with-past out_dir
  ```
- Hoặc dùng repo đã có sẵn thư mục `onnx/` trên Hub (nhiều model có). Phase 0 kiểm tra opus-mt-zh-vi có bản ONNX sẵn hay phải tự export.
- Quy ước: module **chỉ nhận ONNX/safetensors**, không load pickle.

### 2.4 Dependencies Java (chốt version ở Phase 0)
- `com.microsoft.onnxruntime:onnxruntime` (engine chính).
- `ai.djl.huggingface:tokenizers` (tokenizer Rust qua JNI); tên artifact cần kiểm tra lại trên Maven Central, vì sample bạn gửi dùng `hf-tokenizers`, nhiều khả năng sai.
- Jackson (`java-jackson`) để đọc `config.json`, `generation_config.json`, `tokenizer_config.json`.
- Test: JUnit 5, AssertJ, Mockito (đã có trong parent).
- **Không** kéo Spring vào core.

### 2.5 Quyết định cần bạn chốt trước khi bắt đầu
1. Xác nhận OS/arch máy dev và máy chạy thật (Docker/Linux?).
2. Chấp nhận quy ước **ONNX-first**.
3. Model đầu tiên để làm chuẩn: `opus-mt-zh-vi` (translation) và một model embedding nhỏ (ví dụ MiniLM).
4. Có cần tái sử dụng cache HF của Python trên máy không (quyết định dùng layout `~/.cache/huggingface/hub`).

## 3. Kiến trúc

```
modules/java-huggingface/
|-- pom.xml
`-- src/main/java/tungnn/tutor/java/huggingface/
    |-- hub/         ModelRef, ModelRepository, HubCache, HubDownloader
    |-- config/      HfConfig (cacheDir, token, offline, device, threads)
    |-- tokenizer/   HfTokenizer, Encoding
    |-- runtime/     OnnxModelRunner, TensorUtil, Device
    |-- pipeline/    Pipeline<I,O>, Task, HfPipelines, PipelineFactory (SPI)
    |   |-- embedding/  |-- classification/  |-- ner/  `-- seq2seq/
    |-- generation/  GenerationConfig, GreedySearch, BeamSearch, KvCache
    |-- exception/   HfException, ModelNotFoundException, UnsupportedModelException
    `-- demo/        TranslationDemo, EmbeddingDemo
```

**Hai tầng:**
- *Tầng 1 (generic thật):* `ModelRepository`, `HfTokenizer`, `OnnxModelRunner` (nhận/trả `Map<String, Tensor>` theo tên input/output của ONNX graph).
- *Tầng 2 (theo task):* `PipelineFactory` đăng ký qua `ServiceLoader`; thêm task mới không sửa core.

**Nguyên tắc:** `Pipeline` là `AutoCloseable`; model load một lần; class util theo quy ước repo (`final`, constructor private); không singleton ngầm, để caller quản lý vòng đời hoặc dùng `PipelineRegistry` tùy chọn.

## 4. API công khai (phác thảo)

```java
public interface Pipeline<I, O> extends AutoCloseable {
  O run(I input);
  List<O> runBatch(List<I> inputs);
  Task task();
  ModelRef model();
  @Override void close();
}

public final class HfPipelines {
  public static <I, O> Pipeline<I, O> load(Task task, ModelRef model);
  public static <I, O> Pipeline<I, O> load(Task task, ModelRef model, HfConfig config);
}

public record ModelRef(String id, String revision, Path localDir) { /* of("..."), local(Path) */ }
```

Lối đi thô: `OnnxModelRunner.run(Map<String, Tensor>) -> Map<String, Tensor>`.

## 5. Các phase

### Phase 0: Spike và xác nhận prerequisites (1–2 ngày) — cổng quyết định
- Xác nhận OS/arch, JDK 25, cờ native access.
- Chạy tối thiểu một chương trình nhỏ: ONNX Runtime load một model, DJL tokenizer encode/decode.
- Chạy opus-mt-zh-vi: có bản ONNX sẵn trên Hub không, hay export bằng `optimum-cli`. So sánh output với Python trên 10 câu.
- Đo: thời gian load, RAM, ms/câu trên CPU, dung lượng native thêm vào.
- **Deliverable:** báo cáo ngắn chốt version dependency, danh sách native phụ thuộc, các giới hạn phát hiện.
- **Tiêu chí qua cổng:** dịch đúng ≥ 9/10 câu giống Python (cho phép lệch nhỏ do beam), chạy ổn trên máy dev. Nếu **không qua**, dừng và đánh giá lại (đổi engine hoặc sidecar).

### Phase 1: Skeleton và build (0.5–1 ngày)
- Tạo `modules/java-huggingface/pom.xml` (parent `java-infrastructure`).
- Thêm `<module>modules/java-huggingface</module>` vào `pom.xml` root; thêm dependency vào `java-infrastructure-starter/pom.xml`.
- Cấu hình `maven-surefire` đặt `--enable-native-access` và tag `slow` mặc định bị loại.
- `HfConfig`, các exception, khung package.
- **DoD:** `mvn -pl modules/java-huggingface verify` xanh; module nằm trong reactor.

### Phase 2: Tầng 1, hub và tokenizer (2–3 ngày)
- `ModelRepository`: resolve `ModelRef` thành thư mục local (Hub download hoặc dir có sẵn), cache theo layout HF (`models--org--name/snapshots/<rev>`), kiểm tra file bắt buộc (`config.json`, `tokenizer.json`, `*.onnx`).
- Hỗ trợ revision, token, offline, resume/atomic download (ghi vào file tạm rồi đổi tên), kiểm tra kích thước/hash nếu Hub cung cấp.
- `HfTokenizer`: encode, decode, batch, padding, truncation, đọc special tokens.
- **DoD:** test dùng `HttpServer` giả lập Hub + model local nhỏ; tokenizer cho kết quả trùng Python trên bộ câu mẫu (golden file).

### Phase 3: Tầng 1, `OnnxModelRunner` (1–2 ngày)
- Load `OrtSession`, đọc metadata input/output, tạo tensor từ `long[][]`/`float[][]`, chọn device/threads.
- Quản lý native memory: đóng `OrtSession`, `OrtEnvironment`, `OnnxTensor`; tránh leak.
- **DoD:** chạy một model ONNX nhỏ bằng API tensor thô; test đóng/mở lặp nhiều lần không tăng RAM bất thường.

### Phase 4: Pipeline encoder-only (2–3 ngày)
- `EMBEDDING` (mean pooling + normalize, đọc pooling config), `CLASSIFICATION` (softmax + `id2label` từ `config.json`), `TOKEN_CLASSIFICATION`/NER (gộp subword).
- Batching có padding theo batch; `runBatch` hiệu quả.
- Cơ chế SPI `PipelineFactory` + `HfPipelines.load`.
- **DoD:** embedding khớp Python trong sai số (cosine ≥ 0.999); có test song song nhiều luồng.

### Phase 5: Seq2seq generate, dịch/tóm tắt (4–6 ngày) — phần rủi ro cao
- Đọc `generation_config.json` (`decoder_start_token_id`, `eos_token_id`, `max_length`, `num_beams`, `forced_bos_token_id`…).
- Encoder chạy một lần; decoder lặp với KV-cache (`past_key_values`); greedy trước, beam search sau.
- Xử lý kết thúc theo EOS, `max_new_tokens`, batch có độ dài khác nhau.
- `TRANSLATION` và `SUMMARIZATION` dùng chung engine seq2seq.
- **DoD:** opus-mt-zh-vi dịch tập 50 câu cho kết quả trùng/tương đương Python (greedy giống hệt, beam ≥ 95% giống); đo tốc độ chấp nhận được (mục tiêu đặt sau Phase 0).

### Phase 6: Độ bền, hiệu năng, quan sát (2 ngày)
- Pool pipeline cho đa luồng (mô hình `BlockingQueue` như `CrawlApplication`), hoặc chứng minh `OrtSession` dùng chung an toàn.
- Cấu hình thread intra/inter-op, warm-up, giới hạn kích thước batch, timeout.
- Logging (không log nội dung nhạy cảm/token), metric cơ bản (thời gian load/infer).
- Thông báo lỗi rõ cho: thiếu ONNX, task không khớp model, thiếu token cho model gated, hết bộ nhớ.

### Phase 7: Tích hợp vào repo và mở rộng (2–3 ngày)
- `HuggingFaceDictionaryTranslator implements DictionaryTranslator` (trong starter hoặc package tích hợp) cho `Dictionary.doTranslateMissing()`.
- Pipeline dịch PPTX/DOCX nối `PowerPointUtils`/`WordUtils` (`collectTextRuns`, dịch theo batch, `applyTranslations`).
- *Tùy chọn:* adapter Spring AI (`EmbeddingModel`) ở artifact riêng để hoán đổi local/remote; GPU (CUDA/CoreML); thêm task (QA, fill-mask, zero-shot).

### Phase 8: Test, tài liệu, hoàn thiện (1–2 ngày)
- Bộ test: unit (tokenizer, pooling, beam), integration (Hub giả lập), `@Tag("slow")` cho model thật, đối chiếu golden với Python.
- `README` trong module: prerequisites, cách export ONNX, biến môi trường, ví dụ, bảng task/model đã kiểm chứng.
- Demo `TranslationDemo`, `EmbeddingDemo` kiểu `static void main()`.
- **DoD toàn module:** build xanh trên máy dev và một môi trường Linux/Docker; README chạy được từ đầu cho người mới.

## 6. Ước lượng

| Phase | Công |
|---|---|
| 0 Spike | 1–2 ngày |
| 1 Skeleton | 0.5–1 |
| 2 Hub + tokenizer | 2–3 |
| 3 Runner | 1–2 |
| 4 Encoder-only | 2–3 |
| 5 Seq2seq | 4–6 |
| 6 Bền/hiệu năng | 2 |
| 7 Tích hợp | 2–3 |
| 8 Test/docs | 1–2 |
| **Tổng** | **~16–24 ngày công** (Phase 5 và 0 quyết định độ lệch) |

## 7. Rủi ro và cách giảm

| Rủi ro | Mức | Giảm thiểu |
|---|---|---|
| Native lib không có cho OS/arch (đặc biệt macOS x64) | Cao | Phase 0 là cổng quyết định; fallback Linux/Docker hoặc sidecar |
| Seq2seq decode sai/chậm (KV-cache, beam) | Cao | Làm greedy trước, golden test so với Python, tách phase riêng |
| Model không có bản ONNX | Trung bình | Tài liệu hóa `optimum-cli export`; báo lỗi rõ `UnsupportedModelException` |
| Dung lượng/RAM lớn, cold start chậm | Trung bình | Load một lần, warm-up, cache; tài liệu yêu cầu tối thiểu |
| "Generic" bị lạm dụng, API rò rỉ | Trung bình | Chỉ cam kết các task đã kiểm chứng; phần còn lại dùng tensor thô |
| Trùng lặp với `spring-ai-transformers` ở embedding | Thấp | Quyết định ở Phase 4: tái dùng hay tự làm, ghi rõ lý do |
| License model | Thấp | README ghi license từng model đã kiểm chứng |

## 8. Tiêu chí nghiệm thu cuối
1. `HfPipelines.load(Task.TRANSLATION, "Helsinki-NLP/opus-mt-zh-vi").run(...)` hoạt động offline sau lần tải đầu.
2. Có ít nhất 3 task chạy đúng so với Python: embedding, classification, translation.
3. Không rò rỉ tài nguyên native sau vòng lặp load/close.
4. Chạy được trong starter và dịch được một file PPTX mẫu (`SamplePPTX-All.pptx`).
5. Core không phụ thuộc Spring; README đầy đủ prerequisites.

---

**Các điểm tôi muốn bạn review nhất:** (1) cổng Phase 0 và quy ước ONNX-first, (2) việc loại text generation LLM khỏi phạm vi, (3) ước lượng riêng cho Phase 5. Nếu đồng ý, bước tiếp theo là tôi viết code spike Phase 0.