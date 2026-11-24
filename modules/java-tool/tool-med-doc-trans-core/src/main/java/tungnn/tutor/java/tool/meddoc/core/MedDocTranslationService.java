package tungnn.tutor.java.tool.meddoc.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tungnn.tutor.java.ai.client.LlmClient;
import tungnn.tutor.java.infra.document.adapter.markdown.MarkdownDocumentUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;
import tungnn.tutor.java.infra.document.converter.MarkdownDocxConverterUtils;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTranslationResult;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslator;
import tungnn.tutor.java.tool.doctrans.core.infra.translator.document.MarkdownDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.infra.translator.text.LlmTextTranslator;
import tungnn.tutor.java.tool.doctrans.core.infra.translator.text.memory.CsvFileTextTranslationMemory;

public class MedDocTranslationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedDocTranslationService.class);
    private static final int STEPS = 4;
    private static final Map<LanguageCode, String> LANGUAGE_SUFFIX = Map.of(
            LanguageCode.VI, "tieng_Viet",
            LanguageCode.EN, "tieng_Anh",
            LanguageCode.JA, "tieng_Nhat",
            LanguageCode.ZH, "tieng_Trung");

    private final LlmClient llmClient;
    private final TextTranslator textTranslator;

    public MedDocTranslationService(LlmClient llmClient) {
        this(llmClient, new LlmTextTranslator(llmClient));
    }

    public MedDocTranslationService(LlmClient llmClient, TextTranslator textTranslator) {
        this.llmClient = Objects.requireNonNull(llmClient, "llmClient must not be null");
        this.textTranslator = Objects.requireNonNull(textTranslator, "textTranslator must not be null");
    }

    public MedDocReport run(MedDocTranslationRequest request) throws IOException {
        Files.createDirectories(request.outputDir());
        var base = SourceDocumentUtils.baseName(request.source());
        var target = request.targetLanguage();

        LOGGER.info("[1/{}] Converting {} to Markdown", STEPS, request.source().getFileName());
        var source = SourceDocumentUtils.toMarkdown(request.source());

        LOGGER.info("[2/{}] Normalizing and fixing OCR errors", STEPS);
        var cleanup = new OcrCleanup(llmClient).clean(source);
        var suspects = new ArrayList<>(SuspiciousTextUtils.scanSource(cleanup.document()));
        var cleanedPath = request.outputDir().resolve(base + "_ocr.md");
        MarkdownDocumentUtils.writeDocument(cleanup.document(), cleanedPath);

        LOGGER.info("[3/{}] Translating to {}", STEPS, target.languageName());
        var tracking = new TrackingTextTranslator(textTranslator);
        var translated = translate(cleanedPath, target, request.memoryDir(), tracking);
        var skipped = new CopyOnWriteArrayList<String>();
        MarkdownDocument finalDoc = MarkdownDocumentUtils.readDocument(translated);
        if (request.refineVietnamese() && target == LanguageCode.VI) {
            finalDoc = MedRefinePipeline.vietnamese(llmClient, skipped).run(finalDoc);
        }
        suspects.addAll(SuspiciousTextUtils.scanTranslation(finalDoc, target));

        LOGGER.info("[4/{}] Writing the DOCX and the report", STEPS);
        var finalMd = request.outputDir().resolve(base + "_ban_dich.md");
        Files.writeString(finalMd, finalDoc.toMarkdown() + "\n", StandardCharsets.UTF_8);
        var docx = request.outputDir().resolve(base + "_ban_dich_" + LANGUAGE_SUFFIX.get(target) + ".docx");
        MarkdownDocxConverterUtils.convertMarkdownToDocx(finalMd, docx);

        var report = new MedDocReport(
                base,
                target,
                docx,
                cleanup.fixes(),
                tracking.successCount(),
                tracking.failureCount(),
                tracking.firstError(),
                List.copyOf(skipped),
                suspects);
        Files.writeString(request.outputDir().resolve(base + "_bao_cao.md"), report.render(), StandardCharsets.UTF_8);
        return report;
    }

    private Path translate(Path markdown, LanguageCode target, Path memoryDir, TextTranslator translator) {
        var documentTranslator = new MarkdownDocumentTranslator(translator, new CsvFileTextTranslationMemory(memoryDir));
        var result = documentTranslator.translate(new DocumentTranslationRequest.Standard(
                new DocumentTranslationRequest.DocumentSource(markdown),
                target,
                new DocumentTranslationRequest.DocumentTranslationContext(),
                new DocumentTranslationRequest.DocumentTranslationGlossary()));
        return switch (result) {
            case DocumentTranslationResult.Success s -> s.translation().path();
            case DocumentTranslationResult.Failure f ->
                throw new IllegalStateException("Translation failed: " + f.error().message());
        };
    }
}
