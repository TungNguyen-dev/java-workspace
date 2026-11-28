package tungnn.tutor.java.tool.meddoc.core.pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;
import tungnn.tutor.java.tool.meddoc.core.Step;
import tungnn.tutor.java.tool.meddoc.core.chat.MarkdownChat;
import tungnn.tutor.java.tool.meddoc.core.gate.QualityGateUtils;
import tungnn.tutor.java.tool.meddoc.core.refine.MedRefinePipeline;

public final class RefineTargetStepRunner implements StepRunner {

    private final Supplier<MarkdownChat> chat;
    private final AtomicInteger failures;

    public RefineTargetStepRunner(Supplier<MarkdownChat> chat, AtomicInteger failures) {
        this.chat = chat;
        this.failures = failures;
    }

    @Override
    public Step step() {
        return Step.REFINE_TARGET;
    }

    @Override
    public String description(PipelineState state) {
        return "Refining the Vietnamese text";
    }

    @Override
    public Optional<String> skipReason(PipelineState state) {
        return state.target() == LanguageCode.VI ? Optional.empty() : Optional.of(ONLY_VI);
    }

    @Override
    public StepOutcome run(PipelineState state) throws IOException {
        var before = state.document();
        var skippedFrom = state.skipped().size();
        var refined = MedRefinePipeline.vietnamese(chat.get(), state.skipped(), state.chineseSource(), failures)
                .run(before);
        var file = state.write("06_refined.md", refined);
        var details = new ArrayList<String>();
        details.add("LLM failures: " + failures.get());
        StepDetailsUtils.skippedSince(state.skipped(), skippedFrom, details);
        return new StepOutcome(
                QualityGateUtils.refineTarget(before, refined, state.target(), failures.get(), state.gatePolicy()),
                refined,
                file,
                details);
    }
}
