# java-huggingface (HF-local)

Chạy model Hugging Face **cục bộ** bằng ONNX Runtime, API kiểu `pipeline(task, model).run(input)`.
Remote API: dùng Spring AI (ngoài phạm vi module này).

```java
try (Pipeline<String, String> p =
         HfPipelines.load(Task.TRANSLATION, ModelRef.of("<org>/<model-co-onnx>"))) {
  System.out.println(p.run("你好，世界！"));
}
```

## Trạng thái
| Phase | Nội dung                                                     | Trạng thái                            |
|-------|--------------------------------------------------------------|---------------------------------------|
| 1     | Skeleton, config, exception                                  | Đã viết, **chưa compile**             |
| 2     | Hub download/cache, tokenizer                                | Đã viết, **chưa compile**             |
| 3     | `OnnxModelRunner`                                            | Đã viết, **chưa compile**             |
| 4     | Embedding pipeline + SPI                                     | Đã viết, **chưa chạy với model thật** |
| 5     | Seq2seq (greedy, không KV-cache)                             | Đã viết, **chưa chạy với model thật** |
| 5b    | KV-cache, beam search, batch thật                            | Chưa làm                              |
| 6–8   | Pool/thread-safety, tích hợp DictionaryTranslator/PPTX, docs | Chưa làm                              |

## Prerequisites
- JDK 25, Maven (reactor `java-infrastructure`).
- JVM flag: `--enable-native-access=ALL-UNNAMED` (đã đặt cho surefire; thêm khi chạy demo).
- Native lib của ONNX Runtime và DJL tokenizers phải có cho đúng OS/arch (**kiểm tra macOS x64**).
- Mạng tới `huggingface.co` lần đầu; `HF_TOKEN` chỉ cho model gated; `HF_HOME` đổi thư mục cache;
  `HF_HUB_OFFLINE=1` để chạy offline.
- RAM trống >= 4 GB, disk 2-5 GB cho cache.
- Model phải ở dạng **ONNX**. Nếu repo không có thư mục `onnx/`, export một lần (Python):
  ```
  pip install "optimum[onnxruntime]" transformers
  optimum-cli export onnx --model <id> --task text2text-generation <out_dir>   # seq2seq
  optimum-cli export onnx --model <id> --task feature-extraction <out_dir>     # embedding
  ```
  rồi dùng `ModelRef.local(Path.of("<out_dir>"))`.
- Cần `tokenizer.json`. **Lưu ý Marian:** repo gốc `Helsinki-NLP/opus-mt-*` thường chỉ có
  `source.spm`/`target.spm`/`vocab.json`, KHÔNG có `tokenizer.json`. Cần bản đã convert
  (một số repo `Xenova/opus-mt-*`) hoặc dùng SentencePiece (DJL `SpTokenizer`) — xem Phase 0.

## Phase 0 checklist (làm trước khi tin vào code này)
1. Xác nhận tên artifact + version: `ai.djl.huggingface:tokenizers`, `com.microsoft.onnxruntime:onnxruntime`.
2. `mvn -pl modules/java-huggingface verify` (test không cần model).
3. Chạy `EmbeddingDemo` với `sentence-transformers/all-MiniLM-L6-v2` (nếu repo có `onnx/model.onnx`).
4. Chạy `TranslationDemo` với một model Marian đã có tokenizer.json + ONNX; so với Python.
5. Đo thời gian load, RAM, ms/câu.

## Giới hạn đã biết
- Seq2seq chạy lại decoder trên toàn prefix mỗi bước (O(n^2)), greedy, từng câu một.
- Một `tokenizer.json` dùng cho cả encode lẫn decode.
- Pipeline chưa được đảm bảo thread-safe.
- Hub cache dùng layout đơn giản (không symlink/blobs như `huggingface_hub`), chưa kiểm tra hash.
