package tungnn.tutor.java.tool.meddoc.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tungnn.tutor.java.ai.client.LlmClient;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;
import tungnn.tutor.java.tool.doctrans.core.infra.translator.text.DummyTextTranslator;

class MedDocTranslationServiceTest {

    @TempDir
    Path dir;

    @Test
    void shouldRunAllFourStepsAndWriteDocxAndReport() throws IOException {
        var source = dir.resolve("chuong1.md");
        Files.writeString(source, "# 请热剂\n\n本方用于气分热盛证。\n");
        var llm = mock(LlmClient.class);
        when(llm.chat(anyString(), anyString())).thenAnswer(i -> ((String) i.getArgument(1)).replace("请", "清"));
        var service = new MedDocTranslationService(llm, new DummyTextTranslator());

        var report = service.run(
                new MedDocTranslationRequest(source, LanguageCode.VI, dir.resolve("out"), dir.resolve("memory"), false));

        assertThat(report.outputDocx()).hasFileName("chuong1_ban_dich_tieng_Viet.docx").exists();
        assertThat(dir.resolve("out/chuong1_bao_cao.md")).exists();
        assertThat(dir.resolve("out/chuong1_ban_dich.md")).content().contains("清热剂").contains("DUMMY");
        assertThat(report.ocrFixes()).hasSize(1);
        assertThat(report.translatedSegments()).isEqualTo(2);
        assertThat(report.failedSegments()).isZero();
        assertThat(report.render()).contains("Target language: Vietnamese").contains("OCR fix examples");
    }
}
