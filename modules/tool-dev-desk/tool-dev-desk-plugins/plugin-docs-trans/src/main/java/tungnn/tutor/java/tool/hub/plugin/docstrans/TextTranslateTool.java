package tungnn.tutor.java.tool.hub.plugin.docstrans;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.pf4j.Extension;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationResult;
import tungnn.tutor.java.tool.hub.api.ParamSpec;
import tungnn.tutor.java.tool.hub.api.ToolContext;
import tungnn.tutor.java.tool.hub.api.ToolDescriptor;
import tungnn.tutor.java.tool.hub.api.ToolException;
import tungnn.tutor.java.tool.hub.api.ToolExtension;
import tungnn.tutor.java.tool.hub.api.ToolLayout;
import tungnn.tutor.java.tool.hub.api.ToolResult;

@Extension
public class TextTranslateTool implements ToolExtension {
  private volatile TranslatorFactory factory;

  @Override
  public void init(ToolContext ctx) {
    factory = new TranslatorFactory(TransSupport.dataDir(ctx));
  }

  @Override
  public ToolDescriptor descriptor() {
    return new ToolDescriptor(
        "text-trans",
        "Text Translator",
        "Dịch nhanh một đoạn văn bản bằng LLM",
        "translation",
        ToolLayout.TRANSFORM);
  }

  @Override
  public List<ParamSpec> params() {
    TranslatorFactory f =
        factory != null ? factory : new TranslatorFactory(TransSupport.dataDir(null));
    return List.of(
        new ParamSpec(
            "targetLang", TransSupport.LANGUAGE_TYPE, true, "Dịch sang", LanguageCode.VI.name()),
        new ParamSpec(
            "provider",
            TranslatorFactory.providerParamType(),
            true,
            "LLM provider",
            f.defaultProvider()),
        new ParamSpec("model", "string", false, "Model"),
        new ParamSpec("useMemory", "boolean", false, "Bộ nhớ dịch", "true"),
        new ParamSpec("text", "text", true, "Văn bản gốc"));
  }

  @Override
  public ToolResult execute(ToolContext ctx, Map<String, Object> in) throws ToolException {
    String text = in.get("text").toString();
    LanguageCode target = TransSupport.language(in.get("targetLang"));
    String provider = TransSupport.str(in.get("provider"));
    boolean useMemory =
        Boolean.TRUE.equals(in.get("useMemory"))
            && !TranslatorFactory.DUMMY.equalsIgnoreCase(provider);
    TranslatorFactory f =
        factory != null ? factory : new TranslatorFactory(TransSupport.dataDir(ctx));
    return TransSupport.withPluginClassLoader(() -> {
      var memory = useMemory ? TransSupport.memory(ctx) : null;
      if (memory != null) {
        var hit = memory.find(target, text);
        if (hit.isPresent()) {
          return new ToolResult(true, hit.get(), "Lấy từ bộ nhớ dịch (không gọi LLM)");
        }
      }
      var translator = f.create(provider, TransSupport.str(in.get("model")));
      var request =
          new TextTranslationRequest.Standard(
              new TextTranslationRequest.TextUnit(UUID.randomUUID().toString(), text),
              target,
              null,
              null);
      return switch (translator.translate(request)) {
        case TextTranslationResult.Failure fail ->
            throw new ToolException("Dịch thất bại: " + fail.error().message());
        case TextTranslationResult.Success ok -> {
          String translated = ok.translation().translation();
          if (memory != null && translated != null && !translated.isBlank()) {
            memory.save(target, text, translated);
          }
          yield ToolResult.ok(translated);
        }
      };
    });
  }
}
