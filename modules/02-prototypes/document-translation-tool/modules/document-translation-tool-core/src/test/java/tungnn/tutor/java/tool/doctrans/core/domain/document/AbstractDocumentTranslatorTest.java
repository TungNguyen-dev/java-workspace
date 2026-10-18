package tungnn.tutor.java.tool.doctrans.core.domain.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslator;

class AbstractDocumentTranslatorTest {

  @Test
  void shouldBuildTranslationPathCorrectly() {
    // Given
    TextTranslator textTranslator = mock(TextTranslator.class);
    StubDocumentTranslator translator = new StubDocumentTranslator(textTranslator);

    Path sourcePath = Path.of("documents/sample.docx");
    var request =
        new DocumentTranslationRequest.Standard(
            new DocumentTranslationRequest.DocumentSource(sourcePath), LanguageCode.VI, null, null);

    // When
    Path translationPath = translator.buildTranslationPath(request);

    // Then
    assertThat(translationPath).isNotNull();
    assertThat(translationPath.getParent()).isEqualTo(sourcePath.getParent());
    assertThat(translationPath.getFileName().toString()).startsWith("sample_VN_");
    assertThat(translationPath.getFileName().toString()).endsWith(".docx");
  }

  // --- Stub implementation for testing abstract class ---
  private static class StubDocumentTranslator extends AbstractDocumentTranslator<Object> {
    protected StubDocumentTranslator(TextTranslator textTranslator) {
      super(textTranslator);
    }

    @Override
    protected Object openDocument(DocumentTranslationRequest request) {
      return new Object();
    }

    @Override
    protected List<DocumentTextBlock> collectTextBlock(Object document) {
      return List.of();
    }

    @Override
    protected void saveDocument(Path translationPath, Object document) {
      // No-op for test stub
    }
  }
}
