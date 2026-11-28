package tungnn.tutor.java.tool.meddoc.core.glossary;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import tungnn.tutor.java.core.lib.io.resource.ResourceUtils;

public final class ClasspathGlossarySource implements GlossarySource {

    public static final String DEFAULT_RESOURCE = "glossary/glossary.csv";

    private final String resource;

    public ClasspathGlossarySource(String resource) {
        this.resource = Objects.requireNonNull(resource, "resource must not be null");
    }

    public static ClasspathGlossarySource defaults() {
        return new ClasspathGlossarySource(DEFAULT_RESOURCE);
    }

    @Override
    public String describe() {
        return "built-in " + resource;
    }

    @Override
    public Glossary load() throws IOException {
        try (var in = ResourceUtils.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalArgumentException("Glossary resource not found: " + resource);
            }
            return GlossaryFormatUtils.parse(resource, new InputStreamReader(in, StandardCharsets.UTF_8));
        }
    }
}
