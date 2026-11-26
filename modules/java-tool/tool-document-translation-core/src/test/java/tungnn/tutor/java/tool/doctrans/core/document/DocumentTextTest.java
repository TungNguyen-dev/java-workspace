package tungnn.tutor.java.tool.doctrans.core.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class DocumentTextTest {

    private static DocumentText.Block block(String content) {
        return new DocumentText.Block() {
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
        var blocks = new ArrayList<DocumentText.Block>();
        blocks.add(block("a"));
        var segment = new DocumentText.Segment(blocks);

        blocks.add(block("b"));

        assertThat(segment.blocks()).hasSize(1);
    }

    @Test
    void shouldRejectNullTextBlocks() {
        assertThatThrownBy(() -> new DocumentText.Segment(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldReportEmptiness() {
        assertThat(new DocumentText.Segment(List.of()).isEmpty()).isTrue();
        assertThat(new DocumentText.Segment(List.of(block("a"))).isEmpty()).isFalse();
    }
}
