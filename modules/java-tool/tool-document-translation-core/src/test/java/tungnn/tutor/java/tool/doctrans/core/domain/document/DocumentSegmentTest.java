package tungnn.tutor.java.tool.doctrans.core.domain.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class DocumentSegmentTest {

    private static DocumentTextBlock block(String content) {
        return new DocumentTextBlock() {
            @Override
            public String content() {
                return content;
            }

            @Override
            public void setContent(String content) {}
        };
    }

    @Test
    void shouldCopyTextBlocksDefensively() {
        var blocks = new ArrayList<DocumentTextBlock>();
        blocks.add(block("a"));
        var segment = new DocumentSegment(blocks);

        blocks.add(block("b"));

        assertThat(segment.textBlocks()).hasSize(1);
    }

    @Test
    void shouldRejectNullTextBlocks() {
        assertThatThrownBy(() -> new DocumentSegment(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldReportEmptiness() {
        assertThat(new DocumentSegment(List.of()).isEmpty()).isTrue();
        assertThat(new DocumentSegment(List.of(block("a"))).isEmpty()).isFalse();
    }
}
