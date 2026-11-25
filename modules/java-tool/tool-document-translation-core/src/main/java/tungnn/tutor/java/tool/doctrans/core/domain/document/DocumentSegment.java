package tungnn.tutor.java.tool.doctrans.core.domain.document;

import java.util.List;
import java.util.Objects;

public record DocumentSegment(List<DocumentTextBlock> textBlocks) {

    public DocumentSegment {
        Objects.requireNonNull(textBlocks, "textBlocks must not be null");
        textBlocks = List.copyOf(textBlocks);
    }

    public boolean isEmpty() {
        return textBlocks.isEmpty();
    }
}
