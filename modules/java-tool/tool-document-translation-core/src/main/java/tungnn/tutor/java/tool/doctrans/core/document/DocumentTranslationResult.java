package tungnn.tutor.java.tool.doctrans.core.document;

import java.nio.file.Path;

public sealed interface DocumentTranslationResult
        permits DocumentTranslationResult.Success, DocumentTranslationResult.Failure {

    record TranslatedDocument(Path path) {}

    record Success(TranslatedDocument translatedDocument) implements DocumentTranslationResult {}

    record Failure(Error error) implements DocumentTranslationResult {}

    record Error(String code, String message) {}
}
