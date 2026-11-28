package tungnn.tutor.java.tool.meddoc.core.translator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Heading;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Paragraph;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;
import tungnn.tutor.java.tool.meddoc.core.chat.MarkdownChat;

class LlmMarkdownTranslatorTest {

    private static MarkdownDocument doc(MarkdownBlock... blocks) {
        return new MarkdownDocument(List.of(blocks));
    }

    @Test
    void shouldSendOneCallPerSectionAndKeepTheOrder() {
        var llm = mock(MarkdownChat.class);
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
        var llm = mock(MarkdownChat.class);
        when(llm.chat(anyString(), anyString())).thenAnswer(i -> i.getArgument(1));
        var translator = new LlmMarkdownTranslator(llm, 1, 20);
        var source = doc(new Heading(2, "节"), new Paragraph("一二三四五六七八九十"), new Paragraph("甲乙丙丁戊己庚辛壬癸"));

        var result = translator.translate(source, LanguageCode.VI);

        verify(llm, times(2)).chat(anyString(), anyString());
        assertThat(result.blocks()).containsExactlyElementsOf(source.blocks());
    }

    @Test
    void shouldTranslateAWholePrescriptionInOneCall() {
        var llm = mock(MarkdownChat.class);
        when(llm.chat(anyString(), anyString())).thenAnswer(i -> i.getArgument(1));
        var translator = new LlmMarkdownTranslator(llm, 1, 20);
        var source = doc(
                new Heading(3, "白虎汤"),
                new Heading(4, "组成"),
                new Paragraph("石膏一斤知母六两甘草二两"),
                new Heading(4, "用法"),
                new Paragraph("上四味以水一斗煮米熟汤成"));

        var result = translator.translate(source, LanguageCode.VI);

        var sent = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(llm, times(1)).chat(anyString(), sent.capture());
        assertThat(sent.getValue()).contains("白虎汤", "组成", "用法");
        assertThat(result.blocks()).containsExactlyElementsOf(source.blocks());
    }

    @Test
    void shouldSplitAnOversizedPrescriptionAtSubHeadings() {
        var llm = mock(MarkdownChat.class);
        when(llm.chat(anyString(), anyString())).thenAnswer(i -> i.getArgument(1));
        var translator = new LlmMarkdownTranslator(llm, 1, 4000, 25);
        var source = doc(
                new Heading(3, "白虎汤"),
                new Heading(4, "组成"),
                new Paragraph("石膏一斤知母六两甘草二两"),
                new Heading(4, "用法"),
                new Paragraph("上四味以水一斗煮米熟汤成"),
                new Heading(4, "功用"),
                new Paragraph("清热生津"));

        var result = translator.translate(source, LanguageCode.VI);

        var sent = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(llm, times(3)).chat(anyString(), sent.capture());
        assertThat(sent.getAllValues()).allSatisfy(text -> assertThat(text).matches("(?s)^#{3,4} .*"));
        assertThat(result.blocks()).containsExactlyElementsOf(source.blocks());
    }

    @Test
    void shouldSendAnOversizedSubSectionWhole() {
        var llm = mock(MarkdownChat.class);
        when(llm.chat(anyString(), anyString())).thenAnswer(i -> i.getArgument(1));
        var translator = new LlmMarkdownTranslator(llm, 1, 4000, 10);
        var source = doc(
                new Heading(3, "白虎汤"),
                new Heading(4, "方解"),
                new Paragraph("石膏一斤知母六两甘草二两粳米六合"),
                new Paragraph("上四味以水一斗煮米熟汤成"));

        var result = translator.translate(source, LanguageCode.VI);

        verify(llm, times(1)).chat(anyString(), anyString());
        assertThat(result.blocks()).containsExactlyElementsOf(source.blocks());
    }

    @Test
    void shouldKeepTheOriginalSectionWhenTheCallFailsOrIsEmpty() {
        var llm = mock(MarkdownChat.class);
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
        var llm = mock(MarkdownChat.class);
        when(llm.chat(anyString(), anyString())).thenReturn("## A方\n\nA药");
        var translator = new LlmMarkdownTranslator(llm);

        var result = translator.translate(doc(new Heading(3, "甲方"), new Paragraph("甲药")), LanguageCode.VI);

        assertThat(result.blocks()).containsExactly(new Heading(2, "A方"), new Paragraph("A药"));
        assertThat(translator.warnings()).singleElement().asString().contains("heading levels changed");
    }

    @Test
    void shouldReturnListNumbersAsPlainParagraphs() {
        var llm = mock(MarkdownChat.class);
        when(llm.chat(anyString(), anyString())).thenReturn("1. Chứng thiếu dương\n\n2、Hàn nhiệt");
        var translator = new LlmMarkdownTranslator(llm);

        var result = translator.translate(doc(new Paragraph("1、伤寒少阳证"), new Paragraph("2、往来寒热")), LanguageCode.VI);

        assertThat(result.blocks())
                .containsExactly(new Paragraph("1. Chứng thiếu dương"), new Paragraph("2. Hàn nhiệt"));
    }

    @Test
    void shouldEditWithTheEditPromptAndKeepTheOrder() {
        var llm = mock(MarkdownChat.class);
        when(llm.chat(anyString(), anyString()))
                .thenAnswer(i -> ((String) i.getArgument(1)).replace("cho nên", "vì vậy"));
        var polisher = LlmMarkdownTranslator.polisher(llm);
        var source = doc(new Heading(3, "Bạch hổ thang"), new Paragraph("Nhiệt thịnh cho nên khát nước."));

        var result = polisher.polish(source);

        assertThat(result.blocks())
                .containsExactly(new Heading(3, "Bạch hổ thang"), new Paragraph("Nhiệt thịnh vì vậy khát nước."));
        assertThat(polisher.successCount()).isEqualTo(1);
        verify(llm)
                .chat(
                        org.mockito.ArgumentMatchers.argThat(
                                p -> p.contains("biên tập viên") && !p.contains("{targetLanguage}")),
                        anyString());
    }

    @Test
    void shouldKeepTheTranslatedSectionWhenTheEditFails() {
        var llm = mock(MarkdownChat.class);
        when(llm.chat(anyString(), anyString())).thenThrow(new IllegalStateException("boom"));
        var polisher = LlmMarkdownTranslator.polisher(llm);
        var source = doc(new Heading(3, "Bạch hổ thang"), new Paragraph("Nhiệt thịnh."));

        var result = polisher.polish(source);

        assertThat(result.blocks()).containsExactlyElementsOf(source.blocks());
        assertThat(polisher.failureCount()).isEqualTo(1);
    }

    @Test
    void shouldPutTheTargetLanguageInTheSystemPrompt() {
        var llm = mock(MarkdownChat.class);
        when(llm.chat(anyString(), anyString())).thenAnswer(i -> i.getArgument(1));
        var translator = new LlmMarkdownTranslator(llm);

        translator.translate(doc(new Paragraph("甲药")), LanguageCode.EN);

        verify(llm)
                .chat(
                        org.mockito.ArgumentMatchers.argThat(
                                p -> p.contains("English") && !p.contains("{targetLanguage}")),
                        anyString());
    }

    @Test
    void shouldTranslateThroughAnyMarkdownChat() {
        var translator = LlmMarkdownTranslator.of((system, user) -> user.replace("甲", "A"), 1);

        var result = translator.translate(doc(new Heading(1, "章"), new Paragraph("甲药")), LanguageCode.VI);

        assertThat(result.blocks()).containsExactly(new Heading(1, "章"), new Paragraph("A药"));
        assertThat(translator.successCount()).isEqualTo(1);
    }
}
