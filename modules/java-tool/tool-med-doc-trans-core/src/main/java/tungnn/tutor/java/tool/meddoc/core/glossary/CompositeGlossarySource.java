package tungnn.tutor.java.tool.meddoc.core.glossary;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public final class CompositeGlossarySource implements GlossarySource {

    private final List<GlossarySource> sources;

    public CompositeGlossarySource(List<GlossarySource> sources) {
        this.sources = List.copyOf(sources);
    }

    @Override
    public String describe() {
        return sources.stream().map(GlossarySource::describe).collect(Collectors.joining(" + "));
    }

    @Override
    public Glossary load() throws IOException {
        var merged = Glossary.EMPTY;
        for (var source : sources) {
            merged = merged.overriddenBy(source.load());
        }
        return merged;
    }
}
