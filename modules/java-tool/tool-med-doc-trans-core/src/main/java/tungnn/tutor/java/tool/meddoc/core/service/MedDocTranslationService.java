package tungnn.tutor.java.tool.meddoc.core.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tungnn.tutor.java.ai.client.LlmClient;
import tungnn.tutor.java.infra.document.adapter.markdown.MarkdownDocumentUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;
import tungnn.tutor.java.infra.document.converter.MarkdownDocxConverterUtils;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;
import tungnn.tutor.java.tool.meddoc.core.gate.GatePolicy;
import tungnn.tutor.java.tool.meddoc.core.gate.GateResult;
import tungnn.tutor.java.tool.meddoc.core.gate.QualityGateException;
import tungnn.tutor.java.tool.meddoc.core.gate.QualityGateUtils;
import tungnn.tutor.java.tool.meddoc.core.inspect.Suspect;
import tungnn.tutor.java.tool.meddoc.core.inspect.SuspiciousTextUtils;
import tungnn.tutor.java.tool.meddoc.core.ocr.OcrCleanup;
import tungnn.tutor.java.tool.meddoc.core.ocr.OcrFix;
import tungnn.tutor.java.tool.meddoc.core.refine.MedRefinePipeline;
import tungnn.tutor.java.tool.meddoc.core.source.SourceDocumentUtils;
import tungnn.tutor.java.tool.meddoc.core.translator.LlmMarkdownTranslator;

public class MedDocTranslationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedDocTranslationService.class);
    private static final DateTimeFormatter RUN_DIR_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final Map<LanguageCode, String> LANGUAGE_SUFFIX = Map.of(
            LanguageCode.VI, "tieng_Viet",
            LanguageCode.EN, "tieng_Anh",
            LanguageCode.JA, "tieng_Nhat",
            LanguageCode.ZH, "tieng_Trung");

    private final LlmClient llmClient;
    private final GatePolicy gatePolicy;

    public MedDocTranslationService(LlmClient llmClient) {
        this(llmClient, GatePolicy.defaults());
    }

    public MedDocTranslationService(LlmClient llmClient, GatePolicy gatePolicy) {
        this.llmClient = Objects.requireNonNull(llmClient, "llmClient must not be null");
        this.gatePolicy = Objects.requireNonNull(gatePolicy, "gatePolicy must not be null");
    }

    public MedDocReport run(MedDocTranslationRequest request) throws IOException {
        Files.createDirectories(request.outputDir());
        var runDir = createRunDir(request.outputDir());
        var base = SourceDocumentUtils.baseName(request.source());
        var target = request.targetLanguage();
        var steps = request.steps();
        LOGGER.info("Run folder: {} (steps: {})", runDir, steps);

        var translator = new LlmMarkdownTranslator(llmClient);
        var editor = LlmMarkdownTranslator.editor(llmClient);
        var skipped = new CopyOnWriteArrayList<String>();
        var suspects = new ArrayList<Suspect>();
        var gates = new ArrayList<GateResult>();
        var refineTargetFailures = new AtomicInteger();
        List<OcrFix> fixes = List.of();
        MarkdownDocument chineseSource = null;
        Path current = null;
        Path docx = null;
        try {
            LOGGER.info("Loading {} as Markdown", request.source().getFileName());
            var doc = SourceDocumentUtils.toMarkdown(request.source());
            current = writeStep(runDir, "01_source.md", doc);
            boolean needsChinese =
                    request.runs(Step.OCR) || request.runs(Step.REFINE_SOURCE) || request.runs(Step.TRANSLATE);
            check(gates, runDir, QualityGateUtils.load(doc, needsChinese, gatePolicy));

            if (request.runs(Step.OCR)) {
                LOGGER.info("[ocr] Normalizing and fixing OCR errors");
                var cleanup = new OcrCleanup(llmClient).clean(doc);
                fixes = cleanup.fixes();
                var beforeOcr = doc;
                doc = cleanup.document();
                current = writeStep(runDir, "02_ocr.md", doc);
                check(gates, runDir, QualityGateUtils.ocr(beforeOcr, doc, gatePolicy));
            }
            if (request.runs(Step.OCR) || request.runs(Step.REFINE_SOURCE) || request.runs(Step.TRANSLATE)) {
                suspects.addAll(tagged(SuspiciousTextUtils.scanSource(doc), current));
            }

            if (request.runs(Step.REFINE_SOURCE)) {
                LOGGER.info("[refine-source] Structuring the source");
                var beforeRefine = doc;
                var removedHan = new AtomicInteger();
                doc = MedRefinePipeline.chineseSource(skipped, removedHan).run(doc);
                chineseSource = doc;
                current = writeStep(runDir, "03_source_refined.md", doc);
                check(gates, runDir, QualityGateUtils.refineSource(beforeRefine, doc, removedHan.get(), gatePolicy));
            }

            if (request.runs(Step.TRANSLATE)) {
                LOGGER.info("[translate] Translating to {}", target.languageName());
                var beforeTranslate = doc;
                doc = translator.translate(doc, target);
                current = writeStep(runDir, "04_translated.md", doc);
                boolean relabelFollows =
                        request.runs(Step.REFINE_TARGET) && target == LanguageCode.VI && chineseSource != null;
                check(
                        gates,
                        runDir,
                        QualityGateUtils.translate(
                                beforeTranslate,
                                doc,
                                target,
                                translator.failureCount(),
                                translator.warnings(),
                                relabelFollows,
                                gatePolicy));
            }

            if (request.runs(Step.EDIT)) {
                if (target == LanguageCode.VI) {
                    LOGGER.info("[edit] Editing the Vietnamese text into a friendlier style");
                    var beforeEdit = doc;
                    doc = editor.edit(doc);
                    current = writeStep(runDir, "05_edited.md", doc);
                    if (editor.firstError() != null) {
                        skipped.add("edit: %d sections kept as translated, first error: %s"
                                .formatted(editor.failureCount(), editor.firstError()));
                    }
                    check(
                            gates,
                            runDir,
                            QualityGateUtils.edit(
                                    beforeEdit, doc, target, editor.failureCount(), editor.warnings(), gatePolicy));
                } else {
                    skipped.add("edit: only available for the target language VI");
                }
            }

            if (request.runs(Step.REFINE_TARGET)) {
                if (target == LanguageCode.VI) {
                    LOGGER.info("[refine-target] Refining the Vietnamese text");
                    var beforeRefineTarget = doc;
                    doc = MedRefinePipeline.vietnamese(llmClient, skipped, chineseSource, refineTargetFailures)
                            .run(doc);
                    current = writeStep(runDir, "06_refined.md", doc);
                    check(
                            gates,
                            runDir,
                            QualityGateUtils.refineTarget(
                                    beforeRefineTarget, doc, target, refineTargetFailures.get(), gatePolicy));
                } else {
                    skipped.add("refine-target: only available for the target language VI");
                }
            }
            if (request.runs(Step.TRANSLATE) || request.runs(Step.EDIT) || request.runs(Step.REFINE_TARGET)) {
                suspects.addAll(tagged(SuspiciousTextUtils.scanTranslation(doc, target), current));
            }

            if (request.runs(Step.EXPORT)) {
                LOGGER.info("[export] Writing the DOCX");
                docx = runDir.resolve("07_" + base + "_" + LANGUAGE_SUFFIX.get(target) + ".docx");
                MarkdownDocxConverterUtils.convertMarkdownToDocx(current, docx);
                check(gates, runDir, QualityGateUtils.export(docx));
            }
        } catch (IOException | RuntimeException e) {
            var failureReport =
                    writeFailureReport(runDir, base, request, translator, fixes, skipped, suspects, gates, current, e);
            if (e instanceof QualityGateException gateFailure) {
                throw gateFailure.withReport(failureReport);
            }
            throw e;
        }

        var report = new MedDocReport(
                base,
                target,
                docx,
                current,
                runDir,
                steps,
                null,
                fixes,
                translator.successCount(),
                translator.failureCount(),
                translator.firstError(),
                List.copyOf(skipped),
                translator.warnings(),
                suspects,
                List.copyOf(gates));
        Files.writeString(runDir.resolve("report.md"), report.render(), StandardCharsets.UTF_8);
        return report;
    }

    private static void check(List<GateResult> gates, Path runDir, GateResult result) {
        gates.add(result);
        LOGGER.info("Gate {}", result.render());
        if (!result.passed()) {
            throw new QualityGateException(result, runDir);
        }
    }

    // Block indexes refer to different documents (the OCR file vs the translated file), so each suspect names its file.
    private static List<Suspect> tagged(List<Suspect> suspects, Path file) {
        return suspects.stream()
                .map(s -> new Suspect(s.blockIndex(), "[" + file.getFileName() + "] " + s.reason(), s.excerpt()))
                .toList();
    }

    private static String writeFailureReport(
            Path runDir,
            String base,
            MedDocTranslationRequest request,
            LlmMarkdownTranslator translator,
            List<OcrFix> fixes,
            List<String> skipped,
            List<Suspect> suspects,
            List<GateResult> gates,
            Path current,
            Exception failure) {
        var report = new MedDocReport(
                base,
                request.targetLanguage(),
                null,
                current,
                runDir,
                request.steps(),
                String.valueOf(failure.getMessage()),
                fixes,
                translator.successCount(),
                translator.failureCount(),
                translator.firstError(),
                List.copyOf(skipped),
                translator.warnings(),
                suspects,
                List.copyOf(gates));
        var rendered = report.render();
        try {
            Files.writeString(runDir.resolve("report.md"), rendered, StandardCharsets.UTF_8);
        } catch (IOException e) {
            failure.addSuppressed(e);
        }
        return rendered;
    }

    private static Path writeStep(Path runDir, String fileName, MarkdownDocument document) throws IOException {
        var path = runDir.resolve(fileName);
        MarkdownDocumentUtils.writeDocument(document, path);
        return path;
    }

    private static Path createRunDir(Path outputDir) throws IOException {
        var stamp = LocalDateTime.now().format(RUN_DIR_FORMAT);
        for (int attempt = 0; ; attempt++) {
            var dir = outputDir.resolve(attempt == 0 ? stamp : stamp + "_" + attempt);
            try {
                return Files.createDirectory(dir);
            } catch (FileAlreadyExistsException e) {
                // another run took this name within the same second
            }
        }
    }
}
