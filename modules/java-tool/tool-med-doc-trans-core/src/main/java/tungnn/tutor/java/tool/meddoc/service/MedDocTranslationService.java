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
import tungnn.tutor.java.infra.document.converter.SourceMarkdownUtils;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;
import tungnn.tutor.java.tool.meddoc.core.Step;
import tungnn.tutor.java.tool.meddoc.core.glossary.ClasspathGlossarySource;
import tungnn.tutor.java.tool.meddoc.core.glossary.CompositeGlossarySource;
import tungnn.tutor.java.tool.meddoc.core.glossary.GlossarySource;
import tungnn.tutor.java.tool.meddoc.core.ocr.OcrCleanup;
import tungnn.tutor.java.tool.meddoc.core.quality.GatePolicy;
import tungnn.tutor.java.tool.meddoc.core.quality.GateResult;
import tungnn.tutor.java.tool.meddoc.core.quality.QualityGateException;
import tungnn.tutor.java.tool.meddoc.core.step.CheckDosesStepRunner;
import tungnn.tutor.java.tool.meddoc.core.step.ExportStepRunner;
import tungnn.tutor.java.tool.meddoc.core.step.FixOcrStepRunner;
import tungnn.tutor.java.tool.meddoc.core.step.FormatTargetStepRunner;
import tungnn.tutor.java.tool.meddoc.core.step.GlossaryStepRunner;
import tungnn.tutor.java.tool.meddoc.core.step.LoadStepRunner;
import tungnn.tutor.java.tool.meddoc.core.step.PipelineState;
import tungnn.tutor.java.tool.meddoc.core.step.PolishTargetStepRunner;
import tungnn.tutor.java.tool.meddoc.core.step.ScanSourceStepRunner;
import tungnn.tutor.java.tool.meddoc.core.step.ScanTargetStepRunner;
import tungnn.tutor.java.tool.meddoc.core.step.StepRunner;
import tungnn.tutor.java.tool.meddoc.core.step.StructureSourceStepRunner;
import tungnn.tutor.java.tool.meddoc.core.step.TranslateStepRunner;
import tungnn.tutor.java.tool.meddoc.core.translate.LlmMarkdownTranslator;
import tungnn.tutor.java.tool.meddoc.llm.StepChats;
import tungnn.tutor.java.tool.meddoc.service.report.MedDocReport;

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

    private LlmMarkdownTranslator polisherFor(MedDocTranslationRequest request) {
        if (!request.runs(Step.POLISH_TARGET)) {
            return null;
        }
        var chat = chats.forStep(Step.POLISH_TARGET);
        return LlmMarkdownTranslator.polisher(
                chat.chat(),
                chat.maxConcurrency() == null ? LlmMarkdownTranslator.DEFAULT_CONCURRENCY : chat.maxConcurrency());
    }

    private OcrCleanup ocrFor() {
        var chat = chats.forStep(Step.FIX_OCR);
        return chat.maxConcurrency() == null
                ? new OcrCleanup(chat.chat())
                : new OcrCleanup(chat.chat(), chat.maxConcurrency());
    }

    private MedDocTranslationRequest applyClaudeRule(MedDocTranslationRequest request, List<String> skipped) {
        if (!request.runs(Step.POLISH_TARGET) || !chats.usesClaude(Step.TRANSLATE)) {
            return request;
        }
        var remaining = EnumSet.copyOf(request.steps());
        remaining.remove(Step.POLISH_TARGET);
        if (remaining.isEmpty()) {
            throw new IllegalArgumentException(
                    "The polish-target step is skipped because the translation uses Claude, nothing is left to run");
        }
        LOGGER.warn("[polish-target] Skipped: the translation step uses Claude");
        skipped.add("polish-target: skipped because the translation uses Claude");
        return request.withSteps(remaining);
    }

    public MedDocReport run(MedDocTranslationRequest requested) throws IOException {
        var skipped = new CopyOnWriteArrayList<String>();
        var request = applyClaudeRule(requested, skipped);
        Files.createDirectories(request.outputDir());
        var runDir = createRunDir(request.outputDir());
        var base = SourceMarkdownUtils.baseName(request.source());
        var target = request.targetLanguage();
        var steps = request.steps();
        LOGGER.info("Run folder: {} (steps: {})", runDir, steps);

        var recorder = new StepRecorder(chats);
        if (requested.runs(Step.POLISH_TARGET) && !request.runs(Step.POLISH_TARGET)) {
            recorder.skip(Step.POLISH_TARGET, "Skipped because the translation uses Claude");
        }
        var translator = translatorFor(request);
        var runners = runnersFor(request, translator, polisherFor(request));
        var state = new PipelineState(runDir, base, target, steps, gatePolicy, skipped);
        var gates = new ArrayList<GateResult>();
        try {
            for (var step : Step.values()) {
                var runner = runners.get(step);
                if (runner != null) {
                    runStep(runner, state, recorder, gates);
                }
            }
        } catch (IOException | RuntimeException e) {
            recorder.fail(e);
            var failureReport = writeFailureReport(
                    buildReport(request, state, translator, gates, recorder, String.valueOf(e.getMessage())), e);
            if (e instanceof QualityGateException gateFailure) {
                throw gateFailure.withReport(failureReport);
            }
            throw e;
        }

        var report = buildReport(request, state, translator, gates, recorder, null);
        Files.writeString(runDir.resolve("report.md"), report.render(), StandardCharsets.UTF_8);
        return report;
    }

    private MedDocReport buildReport(
            MedDocTranslationRequest request,
            PipelineState state,
            LlmMarkdownTranslator translator,
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
                List.copyOf(state.suspects()),
                List.copyOf(gates),
                chats.usageLines(),
                recorder.build(request.steps()),
                chats.usageTotal());
    }

    private static GlossarySource glossarySourceFor(MedDocTranslationRequest request) {
        if (request.targetLanguage() != LanguageCode.VI) {
            return request.glossarySource();
        }
        if (request.glossarySource() == null) {
            return ClasspathGlossarySource.defaults();
        }
        return new CompositeGlossarySource(List.of(ClasspathGlossarySource.defaults(), request.glossarySource()));
    }

    private Map<Step, StepRunner> runnersFor(
            MedDocTranslationRequest request, LlmMarkdownTranslator translator, LlmMarkdownTranslator polisher) {
        var runners = new EnumMap<Step, StepRunner>(Step.class);
        runners.put(Step.LOAD, new LoadStepRunner(request.source()));
        runners.put(Step.SCAN_SOURCE, new ScanSourceStepRunner());
        runners.put(Step.SCAN_TARGET, new ScanTargetStepRunner());
        if (request.runs(Step.CHECK_DOSES)) {
            runners.put(Step.CHECK_DOSES, new CheckDosesStepRunner());
        }
        if (request.runs(Step.FIX_OCR)) {
            runners.put(Step.FIX_OCR, new FixOcrStepRunner(this::ocrFor));
        }
        if (request.runs(Step.STRUCTURE_SOURCE)) {
            runners.put(Step.STRUCTURE_SOURCE, new StructureSourceStepRunner());
        }
        runners.put(Step.GLOSSARY, new GlossaryStepRunner(glossarySourceFor(request)));
        if (request.runs(Step.TRANSLATE)) {
            runners.put(Step.TRANSLATE, new TranslateStepRunner(translator));
        }
        if (request.runs(Step.POLISH_TARGET)) {
            runners.put(Step.POLISH_TARGET, new PolishTargetStepRunner(polisher));
        }
        if (request.runs(Step.FORMAT_TARGET)) {
            runners.put(
                    Step.FORMAT_TARGET,
                    new FormatTargetStepRunner(
                            () -> chats.forStep(Step.FORMAT_TARGET).chat(), new AtomicInteger()));
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
            if (step.selectable()) {
                state.skipped().add(step.cliName() + ": " + reason);
            }
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
