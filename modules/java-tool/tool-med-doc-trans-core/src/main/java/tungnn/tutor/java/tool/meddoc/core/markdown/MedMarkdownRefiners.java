package tungnn.tutor.java.tool.meddoc.core.markdown;

import java.util.regex.Pattern;
import tungnn.tutor.java.infra.document.adapter.markdown.transform.MarkdownRefiner;

public final class MedMarkdownRefiners {

    private static final Pattern DOSAGE = Pattern.compile("(?<=\\d)(mg|kg|g|ml)\\b");
    private static final Pattern BOOK_BRACKETS = Pattern.compile("[《》]");

    private MedMarkdownRefiners() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static MarkdownRefiner forSource() {
        return MarkdownRefiner.plain();
    }

    public static MarkdownRefiner defaultMedical() {
        return new MarkdownRefiner(DOSAGE, BOOK_BRACKETS);
    }
}
