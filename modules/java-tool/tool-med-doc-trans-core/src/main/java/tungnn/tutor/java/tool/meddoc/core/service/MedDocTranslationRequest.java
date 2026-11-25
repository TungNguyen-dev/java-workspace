package tungnn.tutor.java.tool.meddoc.core.service;

import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;

public record MedDocTranslationRequest(Path source, LanguageCode targetLanguage, Path outputDir, Set<Step> steps) {

    public MedDocTranslationRequest {
        Objects.requireNonNull(source, "source must not be null");
        Objects.requireNonNull(outputDir, "outputDir must not be null");
        targetLanguage = Objects.requireNonNullElse(targetLanguage, LanguageCode.VI);
        var selected = EnumSet.noneOf(Step.class);
        selected.addAll(steps == null ? Step.all() : steps);
        if (selected.isEmpty()) {
            throw new IllegalArgumentException("steps must not be empty");
        }
        steps = selected;
    }

    public boolean runs(Step step) {
        return steps.contains(step);
    }
}
