package tungnn.tutor.java.spring.tool.doctrans.translation_old.core.text.orchestrator;

import java.util.List;
import tungnn.tutor.java.spring.tool.doctrans.translation_old.shared.TextReference;

public interface TextTranslatorOrchestrator {

  TranslationResult translate(List<TextReference> textReferences, TranslationContext context);
}
