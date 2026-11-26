package tungnn.tutor.java.tool.doctrans.core.document;

import java.util.List;
import java.util.Objects;

public sealed interface DocumentText permits DocumentText.Segment, DocumentText.Block {

    non-sealed interface Block extends DocumentText {
        String content();

        void setContent(String content);
    }

    record Segment(List<Block> blocks) implements DocumentText {

        public Segment {
            Objects.requireNonNull(blocks, "blocks must not be null");
            blocks = List.copyOf(blocks);
        }

        public boolean isEmpty() {
            return blocks.isEmpty();
        }
    }
}
