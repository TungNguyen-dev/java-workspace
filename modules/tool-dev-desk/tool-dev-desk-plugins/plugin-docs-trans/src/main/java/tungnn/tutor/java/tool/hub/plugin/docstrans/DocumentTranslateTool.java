package tungnn.tutor.java.tool.hub.plugin.docstrans;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.pf4j.Extension;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTranslationResult;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationMemory;
import tungnn.tutor.java.tool.doctrans.core.infra.translator.document.ExcelDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.infra.translator.document.MarkdownDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.infra.translator.document.SlideShowDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.infra.translator.document.WordDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.infra.translator.text.memory.NoOpTextTranslationMemory;
import tungnn.tutor.java.tool.doctrans.core.service.DocumentTranslationService;
import tungnn.tutor.java.tool.doctrans.core.service.DocumentTranslationServiceImpl;
import tungnn.tutor.java.tool.hub.api.ParamSpec;
import tungnn.tutor.java.tool.hub.api.ToolContext;
import tungnn.tutor.java.tool.hub.api.ToolDescriptor;
import tungnn.tutor.java.tool.hub.api.ToolException;
import tungnn.tutor.java.tool.hub.api.ToolExtension;
import tungnn.tutor.java.tool.hub.api.ToolFile;
import tungnn.tutor.java.tool.hub.api.ToolLayout;
import tungnn.tutor.java.tool.hub.api.ToolResult;

@Extension
public class DocumentTranslateTool implements ToolExtension {

  private static final Map<String, String> CONTENT_TYPES =
      Map.of(
          ".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
          ".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
          ".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
          ".md", "text/markdown; charset=UTF-8",
          ".markdown", "text/markdown; charset=UTF-8",
          ".txt", "text/plain; charset=UTF-8");

  private volatile TranslatorFactory factory;

  private static ToolResult toResult(
      ToolContext ctx,
      Path output,
      String ext,
      TrackingTextTranslator translator,
      String provider,
      LanguageCode target,
      long start)
      throws Exception {
    int ok = translator.successCount();
    int failed = translator.failureCount();
    if (failed > 0 && ok == 0) {
      throw new ToolException(
          "Không dịch được đoạn nào ("
              + failed
              + " lỗi). Lỗi đầu tiên: "
              + translator.firstError());
    }

    byte[] bytes = Files.readAllBytes(output);
    String seconds =
        String.format(Locale.ROOT, "%.1f", (System.currentTimeMillis() - start) / 1000.0);
    StringBuilder msg =
        new StringBuilder("Đã dịch sang ")
            .append(target.languageName())
            .append(" bằng ")
            .append(provider)
            .append(" trong ")
            .append(seconds)
            .append("s — ")
            .append(ok)
            .append(" đoạn gọi LLM");

    if (failed > 0) {
      msg.append(", ")
          .append(failed)
          .append(" đoạn lỗi giữ nguyên bản gốc (")
          .append(translator.firstError())
          .append(')');
    }
    msg.append(". Các đoạn trùng/có trong bộ nhớ dịch không tính.");

    if (ctx != null && ctx.logger() != null) {
      ctx.logger()
          .info("docs-trans {} → {} ({} ok, {} failed)", output.getFileName(), target, ok, failed);
    }

    return new ToolResult(
        true,
        new ToolFile(output.getFileName().toString(), CONTENT_TYPES.get(ext), bytes),
        msg.toString());
  }

  private static String extensionOf(String filename) {
    if (filename == null) return "";
    String name = filename.toLowerCase(Locale.ROOT);
    int dot = name.lastIndexOf('.');
    return dot < 0 ? "" : name.substring(dot);
  }

  private static String safeFileName(String filename) {
    if (filename == null || filename.isBlank()) {
      return "document";
    }
    int slash = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
    String name = filename.substring(slash + 1).replaceAll("[<>:\"/\\\\|?\\x00-\\x1F]", "_");
    return name.isBlank() ? "document" : name;
  }

  @Override
  public void init(ToolContext ctx) {
    factory = new TranslatorFactory(TransSupport.dataDir(ctx));
  }

  @Override
  public ToolDescriptor descriptor() {
    return new ToolDescriptor(
        "docs-trans",
        "Document Translator",
        "Dịch tài liệu Word, Excel, PowerPoint, Markdown bằng LLM, giữ nguyên định dạng",
        "translation",
        ToolLayout.FILE);
  }

  @Override
  public List<ParamSpec> params() {
    return List.of(
        new ParamSpec("file", "file", true, "Tài liệu (.docx, .xlsx, .pptx, .md, .txt)"),
        new ParamSpec(
            "targetLang", TransSupport.LANGUAGE_TYPE, true, "Dịch sang", LanguageCode.VI.name()),
        new ParamSpec(
            "provider",
            TranslatorFactory.providerParamType(),
            true,
            "LLM provider",
            factory().defaultProvider()),
        new ParamSpec("model", "string", false, "Model (bỏ trống = mặc định)"),
        new ParamSpec(
            "useMemory", "boolean", false, "Dùng bộ nhớ dịch (translation memory)", "true"));
  }

  @Override
  public ToolResult execute(ToolContext ctx, Map<String, Object> in) throws ToolException {
    ToolFile file = (ToolFile) in.get("file");
    if (file == null || file.filename() == null) {
      throw new ToolException("Vui lòng cung cấp file cần dịch.");
    }

    String ext = extensionOf(file.filename());
    if (!CONTENT_TYPES.containsKey(ext)) {
      throw new ToolException(
          "Không hỗ trợ file '" + file.filename() + "'. Hỗ trợ: .docx, .xlsx, .pptx, .md, .txt");
    }

    LanguageCode target = TransSupport.language(in.get("targetLang"));
    String provider = TransSupport.str(in.get("provider"));
    String model = TransSupport.str(in.get("model"));
    boolean useMemory =
        Boolean.TRUE.equals(in.get("useMemory"))
            && !TranslatorFactory.DUMMY.equalsIgnoreCase(provider);

    Path jobDir = TransSupport.dataDir(ctx).resolve("jobs").resolve(UUID.randomUUID().toString());
    long start = System.currentTimeMillis();

    try {
      return TransSupport.withPluginClassLoader(() -> {
        var translator = new TrackingTextTranslator(factory().create(provider, model));
        TextTranslationMemory memory =
            useMemory ? TransSupport.memory(ctx) : NoOpTextTranslationMemory.INSTANCE;
        DocumentTranslationService service =
            new DocumentTranslationServiceImpl(
                new WordDocumentTranslator(translator, memory),
                new ExcelDocumentTranslator(translator, memory),
                new SlideShowDocumentTranslator(translator, memory),
                new MarkdownDocumentTranslator(translator, memory));

        Files.createDirectories(jobDir);
        Path input = jobDir.resolve(safeFileName(file.filename()));
        Files.write(input, Objects.requireNonNull(file.content(), "File content cannot be null"));

        DocumentTranslationResult result = service.translateDocument(input, target.name());
        return switch (result) {
          case DocumentTranslationResult.Failure f ->
              throw new ToolException("Dịch thất bại: " + f.error().message());
          case DocumentTranslationResult.Success s ->
              toResult(ctx, s.translation().path(), ext, translator, provider, target, start);
        };
      });
    } finally {
      TransSupport.deleteRecursively(jobDir);
    }
  }

  private TranslatorFactory factory() {
    TranslatorFactory f = factory;
    if (f == null) {
      synchronized (this) {
        f = factory;
        if (f == null) {
          f = new TranslatorFactory(TransSupport.dataDir(null));
          factory = f;
        }
      }
    }
    return f;
  }
}
