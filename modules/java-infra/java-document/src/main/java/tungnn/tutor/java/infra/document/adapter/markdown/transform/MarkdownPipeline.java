package tungnn.tutor.java.infra.document.adapter.markdown.transform;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;

public final class MarkdownPipeline {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarkdownPipeline.class);

    private final List<Step> steps = new ArrayList<>();

    public MarkdownPipeline step(String name, MarkdownAction action) {
        steps.add(new Step(name, action));
        return this;
    }

    public MarkdownDocument run(MarkdownDocument source) {
        var doc = source;
        int i = 1;
        for (var step : steps) {
            int before = doc.blocks().size();
            doc = step.action().apply(doc);
            LOGGER.info(
                    "[{}/{}] {} blocks: {} -> {}", i++, steps.size(), step.name(), before, doc.blocks().size());
        }
        return doc;
    }

    private record Step(String name, MarkdownAction action) {}
}
