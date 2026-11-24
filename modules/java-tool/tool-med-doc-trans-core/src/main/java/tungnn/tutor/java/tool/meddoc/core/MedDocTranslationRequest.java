package tungnn.tutor.java.tool.meddoc.core;

import java.nio.file.Path;
import java.util.Objects;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;

public record MedDocTranslationRequest(
        Path source, LanguageCode targetLanguage, Path outputDir, Path memoryDir, boolean refineVietnamese) {

    public MedDocTranslationRequest {
        Objects.requireNonNull(source, "source must not be null");
        Objects.requireNonNull(outputDir, "outputDir must not be null");
        Objects.requireNonNull(memoryDir, "memoryDir must not be null");
        targetLanguage = Objects.requireNonNullElse(targetLanguage, LanguageCode.VI);
    }
}
