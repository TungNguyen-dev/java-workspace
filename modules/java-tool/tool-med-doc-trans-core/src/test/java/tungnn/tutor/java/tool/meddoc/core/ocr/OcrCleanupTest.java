package tungnn.tutor.java.tool.meddoc.core.ocr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.ai.client.LlmClient;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Heading;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Paragraph;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;

class OcrCleanupTest {

    @Test
    void shouldApplyLlmFixAndRecordIt() {
        var llm = mock(LlmClient.class);
        when(llm.chat(anyString(), anyString())).thenAnswer(i -> ((String) i.getArgument(1)).replace("请热", "清热"));
        var doc = new MarkdownDocument(List.of(new Heading(1, "请热剂"), new Paragraph("石膏一两")));

        var result = new OcrCleanup(llm, 1).clean(doc);

        assertThat(result.document().blocks().get(0)).isEqualTo(new Heading(1, "清热剂"));
        assertThat(result.document().blocks().get(1)).isEqualTo(new Paragraph("石膏一两"));
        assertThat(result.fixes()).singleElement().satisfies(f -> {
            assertThat(f.blockIndex()).isZero();
            assertThat(f.before()).isEqualTo("请热剂");
            assertThat(f.after()).isEqualTo("清热剂");
        });
    }

    @Test
    void shouldKeepOriginalWhenLlmFailsOrReturnsEmpty() {
        var llm = mock(LlmClient.class);
        when(llm.chat(anyString(), any()))
                .thenThrow(new IllegalStateException("boom"))
                .thenReturn("");
        var doc = new MarkdownDocument(List.of(new Paragraph("甲甲甲"), new Paragraph("乙乙乙")));

        var result = new OcrCleanup(llm, 1).clean(doc);

        assertThat(result.document().blocks()).containsExactly(new Paragraph("甲甲甲"), new Paragraph("乙乙乙"));
        assertThat(result.fixes()).isEmpty();
    }
}
