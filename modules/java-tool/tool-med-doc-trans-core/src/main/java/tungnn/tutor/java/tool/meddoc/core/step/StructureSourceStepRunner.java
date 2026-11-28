package tungnn.tutor.java.tool.meddoc.core.step;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import tungnn.tutor.java.tool.meddoc.core.Step;
import tungnn.tutor.java.tool.meddoc.core.quality.QualityGateUtils;
import tungnn.tutor.java.tool.meddoc.core.format.MedFormatPipelines;

public final class StructureSourceStepRunner implements StepRunner {

    @Override
    public Step step() {
        return Step.STRUCTURE_SOURCE;
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
        var refined = MedFormatPipelines.structureSource(state.skipped(), removedHan).run(before);
        state.setChineseSource(refined);
        var file = state.write(step().outputName("md"), refined);
        var details = new ArrayList<String>();
        details.add("Han characters removed: " + removedHan.get());
        StepDetailsUtils.skippedSince(state.skipped(), skippedFrom, details);
        return new StepOutcome(
                QualityGateUtils.structureSource(before, refined, removedHan.get(), state.gatePolicy()),
                refined,
                file,
                details);
    }
}
