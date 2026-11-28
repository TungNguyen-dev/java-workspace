package tungnn.tutor.java.tool.meddoc.core.pipeline;

import java.io.IOException;
import java.util.Optional;
import tungnn.tutor.java.tool.meddoc.core.Step;

public interface StepRunner {

    String ONLY_VI = "only available for the target language VI";

    Step step();

    String description(PipelineState state);

    default Optional<String> skipReason(PipelineState state) {
        return Optional.empty();
    }

    StepOutcome run(PipelineState state) throws IOException;
}
