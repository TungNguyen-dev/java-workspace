package tungnn.tutor.java.tool.meddoc.core.glossary;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class FileGlossarySource implements GlossarySource {

    private final Path file;

    public FileGlossarySource(Path file) {
        this.file = Objects.requireNonNull(file, "file must not be null");
    }

    @Override
    public String describe() {
        return "file " + file.getFileName();
    }

    @Override
    public Glossary load() throws IOException {
        if (!Files.isRegularFile(file)) {
            throw new IllegalArgumentException("Glossary file not found: " + file);
        }
        try (var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return GlossaryFormatUtils.parse(file.getFileName().toString(), reader);
        }
    }
}
