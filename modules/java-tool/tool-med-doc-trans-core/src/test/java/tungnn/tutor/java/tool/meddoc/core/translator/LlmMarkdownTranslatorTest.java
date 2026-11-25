package tungnn.tutor.java.tool.meddoc.core.translator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.ai.client.LlmClient;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Heading;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Paragraph;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;

class LlmMarkdownTranslatorTest {

    private static MarkdownDocument doc(MarkdownBlock... blocks) {
        return new MarkdownDocument(List.of(blocks));
    }

    @Test
    void shouldSendOneCallPerSectionAndKeepTheOrder() {
        var llm = mock(LlmClient.class);
        when(llm.chat(anyString(), anyString()))
                .thenAnswer(i -> ((String) i.getArgument(1)).replace("甲", "A").replace("乙", "B"));
        var translator = new LlmMarkdownTranslator(llm);
        var source = doc(
                new Heading(1, "章"),
                new Paragraph("导言"),
                new Heading(3, "甲方"),
                new Heading(4, "组成"),
                new Paragraph("甲药"),
                new Heading(3, "乙方"),
                new Paragraph("乙药"));

        var result = translator.translate(source, LanguageCode.VI);

        verify(llm, times(3)).chat(anyString(), anyString());
        assertThat(result.blocks())
                .containsExactly(
                        new Heading(1, "章"),
                        new Paragraph("导言"),
                        new Heading(3, "A方"),
                        new Heading(4, "组成"),
                        new Paragraph("A药"),
                        new Heading(3, "B方"),
                        new Paragraph("B药"));
        assertThat(translator.successCount()).isEqualTo(3);
        assertThat(translator.failureCount()).isZero();
    }

    @Test
    void shouldSplitALongSectionBetweenBlocks() {
        var llm = mock(LlmClient.class);
        when(llm.chat(anyString(), anyString())).thenAnswer(i -> i.getArgument(1));
        var translator = new LlmMarkdownTranslator(llm, 1, 20);
        var source = doc(new Heading(3, "方"), new Paragraph("一二三四五六七八九十"), new Paragraph("甲乙丙丁戊己庚辛壬癸"));

        var result = translator.translate(source, LanguageCode.VI);

        verify(llm, times(2)).chat(anyString(), anyString());
        assertThat(result.blocks()).containsExactlyElementsOf(source.blocks());
    }

    @Test
    void shouldKeepTheOriginalSectionWhenTheCallFailsOrIsEmpty() {
        var llm = mock(LlmClient.class);
        when(llm.chat(anyString(), anyString())).thenAnswer(i -> {
            String text = i.getArgument(1);
            if (text.contains("甲")) {
                throw new IllegalStateException("boom");
            }
            return "  ";
        });
        var translator = new LlmMarkdownTranslator(llm, 1, 4000);
        var source = doc(new Heading(3, "甲方"), new Paragraph("甲药"), new Heading(3, "乙方"), new Paragraph("乙药"));

        var result = translator.translate(source, LanguageCode.VI);

        assertThat(result.blocks()).containsExactlyElementsOf(source.blocks());
        assertThat(translator.failureCount()).isEqualTo(2);
        assertThat(translator.successCount()).isZero();
        assertThat(translator.firstError()).isNotBlank();
    }

    @Test
    void shouldWarnWhenTheHeadingLevelsChange() {
        var llm = mock(LlmClient.class);
        when(llm.chat(anyString(), anyString())).thenReturn("## A方\n\nA药");
        var translator = new LlmMarkdownTranslator(llm);

        var result = translator.translate(doc(new Heading(3, "甲方"), new Paragraph("甲药")), LanguageCode.VI);

        assertThat(result.blocks()).containsExactly(new Heading(2, "A方"), new Paragraph("A药"));
        assertThat(translator.warnings()).singleElement().asString().contains("heading levels changed");
    }

    @Test
    void shouldReturnListNumbersAsPlainParagraphs() {
        var llm = mock(LlmClient.class);
        when(llm.chat(anyString(), anyString())).thenReturn("1. Chứng thiếu dương\n\n2、Hàn nhiệt");
        var translator = new LlmMarkdownTranslator(llm);

        var result = translator.translate(doc(new Paragraph("1、伤寒少阳证"), new Paragraph("2、往来寒热")), LanguageCode.VI);

        assertThat(result.blocks())
                .containsExactly(new Paragraph("1. Chứng thiếu dương"), new Paragraph("2. Hàn nhiệt"));
    }

    @Test
    void shouldPutTheTargetLanguageInTheSystemPrompt() {
        var llm = mock(LlmClient.class);
        when(llm.chat(anyString(), anyString())).thenAnswer(i -> i.getArgument(1));
        var translator = new LlmMarkdownTranslator(llm);

        translator.translate(doc(new Paragraph("甲药")), LanguageCode.EN);

        verify(llm)
                .chat(
                        org.mockito.ArgumentMatchers.argThat(
                                p -> p.contains("English") && !p.contains("{targetLanguage}")),
                        anyString());
    }
}
