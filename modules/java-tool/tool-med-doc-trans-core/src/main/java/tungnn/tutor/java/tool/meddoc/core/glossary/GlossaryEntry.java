package tungnn.tutor.java.tool.meddoc.core.glossary;

public record GlossaryEntry(String source, String target, String context) {

    public GlossaryEntry {
        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException("Glossary source term must not be blank");
        }
        if (target == null || target.isBlank()) {
            throw new IllegalArgumentException("Glossary translation must not be blank for '" + source + "'");
        }
        source = source.strip();
        target = target.strip();
        context = context == null || context.isBlank() ? null : context.strip();
    }

    public GlossaryEntry(String source, String target) {
        this(source, target, null);
    }

    public boolean hasContext() {
        return context != null;
    }
}
