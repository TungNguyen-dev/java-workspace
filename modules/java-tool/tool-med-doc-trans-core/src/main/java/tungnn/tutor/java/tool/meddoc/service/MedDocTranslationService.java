package tungnn.tutor.java.tool.meddoc.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tungnn.tutor.java.tool.meddoc.core.Step;
import tungnn.tutor.java.tool.meddoc.core.gate.GatePolicy;
import tungnn.tutor.java.tool.meddoc.core.gate.GateResult;
import tungnn.tutor.java.tool.meddoc.core.gate.QualityGateException;
import tungnn.tutor.java.tool.meddoc.core.gate.QualityGateUtils;
import tungnn.tutor.java.tool.meddoc.core.inspect.Suspect;
import tungnn.tutor.java.tool.meddoc.core.inspect.SuspiciousTextUtils;
import tungnn.tutor.java.tool.meddoc.core.ocr.OcrCleanup;
import tungnn.tutor.java.tool.meddoc.core.ocr.OcrFix;
import tungnn.tutor.java.tool.meddoc.core.pipeline.EditStepRunner;
import tungnn.tutor.java.tool.meddoc.core.pipeline.ExportStepRunner;
import tungnn.tutor.java.tool.meddoc.core.pipeline.OcrStepRunner;
import tungnn.tutor.java.tool.meddoc.core.pipeline.PipelineState;
import tungnn.tutor.java.tool.meddoc.core.pipeline.RefineSourceStepRunner;
import tungnn.tutor.java.tool.meddoc.core.pipeline.RefineTargetStepRunner;
import tungnn.tutor.java.tool.meddoc.core.pipeline.StepRunner;
import tungnn.tutor.java.tool.meddoc.core.pipeline.TranslateStepRunner;
import tungnn.tutor.java.tool.meddoc.core.translator.LlmMarkdownTranslator;
import tungnn.tutor.java.tool.meddoc.llm.StepChats;
import tungnn.tutor.java.tool.meddoc.service.report.MedDocReport;
import tungnn.tutor.java.tool.meddoc.service.report.StepReport;
import tungnn.tutor.java.tool.meddoc.utils.SourceDocumentUtils;

public class MedDocTranslationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedDocTranslationService.class);
    private static final DateTimeFormatter RUN_DIR_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final StepChats chats;
    private final GatePolicy gatePolicy;

    public MedDocTranslationService(StepChats chats) {
        this(chats, GatePolicy.defaults());
    }

    public MedDocTranslationService(StepChats chats, GatePolicy gatePolicy) {
        this.chats = Objects.requireNonNull(chats, "chats must not be null");
        this.gatePolicy = Objects.requireNonNull(gatePolicy, "gatePolicy must not be null");
    }

    private LlmMarkdownTranslator translatorFor(MedDocTranslationRequest request) {
        if (!request.runs(Step.TRANSLATE)) {
            // Only its counters are read when the translate step is not selected
            return LlmMarkdownTranslator.of(
                    (system, user) -> {
                        throw new IllegalStateException("The translate step is not selected");
                    },
                    1);
        }
        var chat = chats.forStep(Step.TRANSLATE);
        return chat.maxConcurrency() == null
                ? new LlmMarkdownTranslator(chat.chat())
                : LlmMarkdownTranslator.of(chat.chat(), chat.maxConcurrency());
    }

    private LlmMarkdownTranslator editorFor(MedDocTranslationRequest request) {
        if (!request.runs(Step.EDIT)) {
            return null;
        }
        var chat = chats.forStep(Step.EDIT);
        return LlmMarkdownTranslator.editor(
                chat.chat(),
                chat.maxConcurrency() == null ? LlmMarkdownTranslator.DEFAULT_CONCURRENCY : chat.maxConcurrency());
    }

    private OcrCleanup ocrFor() {
        var chat = chats.forStep(Step.OCR);
        return chat.maxConcurrency() == null
                ? new OcrCleanup(chat.chat())
                : new OcrCleanup(chat.chat(), chat.maxConcurrency());
    }

    private MedDocTranslationRequest applyClaudeRule(MedDocTranslationRequest request, List<String> skipped) {
        if (!request.runs(Step.EDIT) || !chats.usesClaude(Step.TRANSLATE)) {
            return request;
        }
        var remaining = EnumSet.copyOf(request.steps());
        remaining.remove(Step.EDIT);
        if (remaining.isEmpty()) {
            throw new IllegalArgumentException(
                    "The edit step is skipped because the translation uses Claude, nothing is left to run");
        }
        LOGGER.warn("[edit] Skipped: the translation step uses Claude");
        skipped.add("edit: skipped because the translation uses Claude");
        return request.withSteps(remaining);
    }

    public MedDocReport run(MedDocTranslationRequest requested) throws IOException {
        var skipped = new CopyOnWriteArrayList<String>();
        var request = applyClaudeRule(requested, skipped);
        Files.createDirectories(request.outputDir());
        var runDir = createRunDir(request.outputDir());
        var base = SourceDocumentUtils.baseName(request.source());
        var target = request.targetLanguage();
        var steps = request.steps();
        LOGGER.info("Run folder: {} (steps: {})", runDir, steps);

        var recorder = new StepRecorder(chats);
        if (requested.runs(Step.EDIT) && !request.runs(Step.EDIT)) {
            recorder.skip(Step.EDIT, "Skipped because the translation uses Claude");
        }
        var translator = translatorFor(request);
        var runners = runnersFor(request, translator, editorFor(request));
        var state = new PipelineState(runDir, base, target, steps, gatePolicy, skipped);
        var suspects = new ArrayList<Suspect>();
        var gates = new ArrayList<GateResult>();
        try {
            LOGGER.info("Loading {} as Markdown", request.source().getFileName());
            recorder.begin(StepRecorder.LOAD, null, null);
            var doc = SourceDocumentUtils.toMarkdown(request.source());
            state.write("01_source.md", doc);
            boolean needsChinese =
                    request.runs(Step.OCR) || request.runs(Step.REFINE_SOURCE) || request.runs(Step.TRANSLATE);
            check(
                    gates,
                    runDir,
                    recorder.finish(
                            QualityGateUtils.load(doc, needsChinese, gatePolicy),
                            doc,
                            state.current(),
                            List.of("Source: " + request.source().getFileName())));

            for (var step : Step.values()) {
                if (step == Step.REFINE_SOURCE && needsChinese) {
                    suspects.addAll(tagged(SuspiciousTextUtils.scanSource(state.document()), state.current()));
                }
                if (step == Step.EXPORT
                        && (request.runs(Step.TRANSLATE) || request.runs(Step.EDIT) || request.runs(Step.REFINE_TARGET))) {
                    suspects.addAll(
                            tagged(SuspiciousTextUtils.scanTranslation(state.document(), target), state.current()));
                }
                var runner = runners.get(step);
                if (runner != null) {
                    runStep(runner, state, recorder, gates);
                }
            }
        } catch (IOException | RuntimeException e) {
            recorder.fail(e);
            var failureReport = writeFailureReport(
                    buildReport(request, state, translator, suspects, gates, recorder, String.valueOf(e.getMessage())),
                    e);
            if (e instanceof QualityGateException gateFailure) {
                throw gateFailure.withReport(failureReport);
            }
            throw e;
        }

        var report = buildReport(request, state, translator, suspects, gates, recorder, null);
        Files.writeString(runDir.resolve("report.md"), report.render(), StandardCharsets.UTF_8);
        return report;
    }

    private MedDocReport buildReport(
            MedDocTranslationRequest request,
            PipelineState state,
            LlmMarkdownTranslator translator,
            List<Suspect> suspects,
            List<GateResult> gates,
            StepRecorder recorder,
            String failure) {
        return new MedDocReport(
                state.baseName(),
                request.targetLanguage(),
                failure == null ? state.docx() : null,
                state.current(),
                state.runDir(),
                request.steps(),
                failure,
                state.fixes(),
                translator.successCount(),
                translator.failureCount(),
                translator.firstError(),
                List.copyOf(state.skipped()),
                translator.warnings(),
                suspects,
                List.copyOf(gates),
                chats.usageLines(),
                recorder.build(request.steps()),
                chats.usageTotal());
    }

    private Map<Step, StepRunner> runnersFor(
            MedDocTranslationRequest request, LlmMarkdownTranslator translator, LlmMarkdownTranslator editor) {
        var runners = new EnumMap<Step, StepRunner>(Step.class);
        if (request.runs(Step.OCR)) {
            runners.put(Step.OCR, new OcrStepRunner(this::ocrFor));
        }
        if (request.runs(Step.REFINE_SOURCE)) {
            runners.put(Step.REFINE_SOURCE, new RefineSourceStepRunner());
        }
        if (request.runs(Step.TRANSLATE)) {
            runners.put(Step.TRANSLATE, new TranslateStepRunner(translator));
        }
        if (request.runs(Step.EDIT)) {
            runners.put(Step.EDIT, new EditStepRunner(editor));
        }
        if (request.runs(Step.REFINE_TARGET)) {
            runners.put(
                    Step.REFINE_TARGET,
                    new RefineTargetStepRunner(() -> chats.forStep(Step.REFINE_TARGET).chat(), new AtomicInteger()));
        }
        if (request.runs(Step.EXPORT)) {
            runners.put(Step.EXPORT, new ExportStepRunner());
        }
        return runners;
    }

    private static void runStep(StepRunner runner, PipelineState state, StepRecorder recorder, List<GateResult> gates)
            throws IOException {
        var step = runner.step();
        var skipReason = runner.skipReason(state);
        if (skipReason.isPresent()) {
            var reason = skipReason.get();
            state.skipped().add(step.cliName() + ": " + reason);
            recorder.skip(step, Character.toUpperCase(reason.charAt(0)) + reason.substring(1));
            return;
        }
        LOGGER.info("[{}] {}", step.cliName(), runner.description(state));
        recorder.begin(step.cliName(), step, state.document());
        var outcome = runner.run(state);
        check(
                gates,
                state.runDir(),
                recorder.finish(outcome.gate(), outcome.document(), outcome.output(), outcome.details()));
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

    private static String writeFailureReport(MedDocReport report, Exception failure) {
        var rendered = report.render();
        try {
            Files.writeString(report.runDir().resolve("report.md"), rendered, StandardCharsets.UTF_8);
        } catch (IOException e) {
            failure.addSuppressed(e);
        }
        return rendered;
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
