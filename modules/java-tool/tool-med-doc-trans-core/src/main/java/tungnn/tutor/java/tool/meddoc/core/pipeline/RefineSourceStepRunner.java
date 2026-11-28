package tungnn.tutor.java.tool.meddoc.core.pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import tungnn.tutor.java.tool.meddoc.core.Step;
import tungnn.tutor.java.tool.meddoc.core.gate.QualityGateUtils;
import tungnn.tutor.java.tool.meddoc.core.refine.MedRefinePipeline;

public final class RefineSourceStepRunner implements StepRunner {

    @Override
    public Step step() {
        return Step.REFINE_SOURCE;
    }

    @Override
    public String description(PipelineState state) {
        return "Structuring the source";
    }

    @Override
    public StepOutcome run(PipelineState state) throws IOException {
        var before = state.document();
        var removedHan = new AtomicInteger();
        var skippedFrom = state.skipped().size();
        var refined = MedRefinePipeline.chineseSource(state.skipped(), removedHan).run(before);
        state.setChineseSource(refined);
        var file = state.write("03_source_refined.md", refined);
        var details = new ArrayList<String>();
        details.add("Han characters removed: " + removedHan.get());
        StepDetailsUtils.skippedSince(state.skipped(), skippedFrom, details);
        return new StepOutcome(
                QualityGateUtils.refineSource(before, refined, removedHan.get(), state.gatePolicy()),
                refined,
                file,
                details);
    }
}
