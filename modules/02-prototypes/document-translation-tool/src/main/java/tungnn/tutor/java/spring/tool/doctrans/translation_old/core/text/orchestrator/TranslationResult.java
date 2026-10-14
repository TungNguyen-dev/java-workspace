package tungnn.tutor.java.spring.tool.doctrans.translation_old.core.text.orchestrator;

import java.util.List;
import tungnn.tutor.java.spring.tool.doctrans.translation_old.shared.TextReference;

public record TranslationResult(List<Entry> translations) {

  public static TranslationResult empty() {
    return new TranslationResult(List.of());
  }

  public record Entry(TextReference textReference, String translatedText) {}
}
