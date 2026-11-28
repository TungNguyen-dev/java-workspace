package tungnn.tutor.java.tool.meddoc.core.step;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;
import tungnn.tutor.java.tool.meddoc.core.Step;
import tungnn.tutor.java.ai.chat.TextChat;
import tungnn.tutor.java.tool.meddoc.core.quality.QualityGateUtils;
import tungnn.tutor.java.tool.meddoc.core.format.MedFormatPipelines;

public final class FormatTargetStepRunner implements StepRunner {

    private final Supplier<TextChat> chat;
    private final AtomicInteger failures;

    public FormatTargetStepRunner(Supplier<TextChat> chat, AtomicInteger failures) {
        this.chat = chat;
        this.failures = failures;
    }

    @Override
    public Step step() {
        return Step.FORMAT_TARGET;
    }

    @Override
    public String description(PipelineState state) {
        return "Formatting sections with an LLM";
    }

    @Override
    public Optional<String> skipReason(PipelineState state) {
        return state.target() == LanguageCode.VI ? Optional.empty() : Optional.of(ONLY_VI);
    }

    @Override
    public StepOutcome run(PipelineState state) throws IOException {
        var before = state.document();
        var skippedFrom = state.skipped().size();
        var refined = MedFormatPipelines.formatTarget(chat.get(), state.skipped(), failures)
                .run(before);
        var file = state.write(step().outputName("md"), refined);
        var details = new ArrayList<String>();
        details.add("LLM failures: " + failures.get());
        StepDetailsUtils.skippedSince(state.skipped(), skippedFrom, details);
        return new StepOutcome(
                QualityGateUtils.formatTarget(before, refined, state.target(), failures.get(), state.gatePolicy()),
                refined,
                file,
                details);
    }
}
