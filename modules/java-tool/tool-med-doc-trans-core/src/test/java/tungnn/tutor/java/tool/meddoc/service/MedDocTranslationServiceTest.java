package tungnn.tutor.java.tool.meddoc.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;
import tungnn.tutor.java.tool.meddoc.core.Step;
import tungnn.tutor.java.tool.meddoc.core.chat.MarkdownChat;
import tungnn.tutor.java.tool.meddoc.core.gate.GatePolicy;
import tungnn.tutor.java.tool.meddoc.core.gate.QualityGateException;
import tungnn.tutor.java.tool.meddoc.core.translator.LlmMarkdownTranslator;
import tungnn.tutor.java.tool.meddoc.llm.StepChat;
import tungnn.tutor.java.tool.meddoc.llm.StepChats;

class MedDocTranslationServiceTest {

    @TempDir
    Path dir;

    private static MarkdownChat llm(UnaryOperator<String> ocr, UnaryOperator<String> translate) {
        var llm = mock(MarkdownChat.class);
        when(llm.chat(anyString(), anyString())).thenAnswer(i -> {
            String system = i.getArgument(0);
            String user = i.getArgument(1);
            if (system.contains("dịch giả")) {
                return translate.apply(user);
            }
            return system.contains("hiệu đính") ? ocr.apply(user) : user;
        });
        return llm;
    }

    private static StepChats chats(MarkdownChat chat) {
        return StepChats.of(step -> StepChat.of(chat));
    }

    private static MedDocTranslationService service(MarkdownChat chat) {
        return new MedDocTranslationService(chats(chat));
    }

    private static MedDocTranslationService lenient(MarkdownChat llm) {
        var defaults = GatePolicy.defaults();
        return new MedDocTranslationService(
                chats(llm),
                new GatePolicy(
                        defaults.minSourceHan(),
                        defaults.minOcrHanRetention(),
                        defaults.minRefineSourceHanRetention(),
                        Integer.MAX_VALUE,
                        defaults.minBlockRatio(),
                        defaults.maxBlockRatio(),
                        1.0,
                        defaults.maxRefineTargetLlmFailures()));
    }

    private MedDocTranslationRequest request(Path source, LanguageCode target, EnumSet<Step> steps) {
        return new MedDocTranslationRequest(source, target, dir.resolve("out"), steps);
    }

    @Test
    void shouldWriteOneNumberedFilePerSelectedStep() throws IOException {
        var source = dir.resolve("chuong1.md");
        Files.writeString(source, "# 请热剂\n\n本方用于气分热盛证。\n");
        var service =
                lenient(llm(text -> text.replace("请", "清"), text -> text.replace("清热剂", "Bài thuốc thanh nhiệt")));

        var report = service.run(request(source, LanguageCode.VI, EnumSet.of(Step.OCR, Step.TRANSLATE, Step.EXPORT)));

        var runDir = singleRunDir();
        assertThat(runDir.getFileName().toString()).matches("\\d{8}_\\d{6}(_\\d+)?");
        assertThat(report.runDir()).isEqualTo(runDir);
        assertThat(report.outputDocx())
                .hasFileName("07_chuong1_tieng_Viet.docx")
                .exists();
        assertThat(fileNames(runDir))
                .containsExactlyInAnyOrder(
                        "01_source.md", "02_ocr.md", "04_translated.md", "07_chuong1_tieng_Viet.docx", "report.md");
        assertThat(runDir.resolve("02_ocr.md")).content().contains("清热剂");
        assertThat(runDir.resolve("04_translated.md")).content().contains("Bài thuốc thanh nhiệt");
        assertThat(report.ocrFixes()).hasSize(1);
        assertThat(report.translatedSections()).isEqualTo(1);
        assertThat(report.failedSections()).isZero();
        assertThat(report.render())
                .contains("Target language: Vietnamese")
                .contains("Steps: ocr, translate, export")
                .contains("Run folder: " + runDir)
                .contains("## Steps")
                .contains("### 1. load — done")
                .contains("### 2. ocr — done")
                .contains("### 3. refine-source — not selected")
                .contains("### 4. translate — done")
                .contains("### 7. export — done")
                .contains("- LLM: not used")
                .contains("## LLM usage")
                .contains("ocr: 2 calls, 0 failed, $n/a, tokens not reported by provider")
                .contains("total: 3 calls");
    }

    @Test
    void shouldStructureTranslateAndRelabelWithAllSteps() throws IOException {
        var source = dir.resolve("chuong2.md");
        Files.writeString(source, "第三章\n\n和解剂\n\n小柴胡汤\n\n【组成】柴胡半斤\n");
        var service = service(llm(
                text -> text,
                text -> text.replace("第三章 和解剂", "Chương 3 Bài thuốc hòa giải")
                        .replace("小柴胡汤", "Tiểu sài hồ thang")
                        .replace("组成", "Thành phần bài thuốc")
                        .replace("柴胡半斤", "Sài hồ nửa cân")));

        service.run(request(source, LanguageCode.VI, EnumSet.copyOf(Step.all())));

        var runDir = singleRunDir();
        assertThat(runDir.resolve("03_source_refined.md"))
                .content()
                .contains("# 第三章 和解剂")
                .contains("### 小柴胡汤")
                .contains("#### 组成");
        assertThat(runDir.resolve("04_translated.md")).content().contains("#### Thành phần bài thuốc");
        assertThat(runDir.resolve("05_edited.md")).content().contains("#### Thành phần bài thuốc");
        assertThat(runDir.resolve("06_refined.md")).content().contains("#### Thành phần\n");
    }

    @Test
    void shouldEditTheTranslationWithTheEditPrompt() throws IOException {
        var source = dir.resolve("chuong12.md");
        Files.writeString(source, "# 清热剂\n\n本方用于气分热盛证。\n");
        var llm = mock(MarkdownChat.class);
        when(llm.chat(anyString(), anyString())).thenAnswer(i -> {
            String system = i.getArgument(0);
            String user = i.getArgument(1);
            if (system.contains("biên tập viên")) {
                return user.replace("Bài thuốc thanh nhiệt", "Bài thuốc thanh nhiệt (đã biên tập)");
            }
            return user.replace("清热剂", "Bài thuốc thanh nhiệt");
        });
        var service = lenient(llm);

        var report = service.run(request(source, LanguageCode.VI, EnumSet.of(Step.TRANSLATE, Step.EDIT)));

        var runDir = singleRunDir();
        assertThat(fileNames(runDir))
                .containsExactlyInAnyOrder("01_source.md", "04_translated.md", "05_edited.md", "report.md");
        assertThat(runDir.resolve("05_edited.md")).content().contains("Bài thuốc thanh nhiệt (đã biên tập)");
        assertThat(report.finalFile()).hasFileName("05_edited.md");
        assertThat(report.render()).contains("PASS edit");
    }

    @Test
    void shouldSkipTheEditStepForOtherTargetLanguages() throws IOException {
        var source = dir.resolve("chuong13.md");
        Files.writeString(source, "# 清热剂\n\n本方用于气分热盛证。\n");
        var service = lenient(llm(text -> text, text -> text.replace("清热剂", "Heat-clearing")));

        var report = service.run(request(source, LanguageCode.EN, EnumSet.of(Step.TRANSLATE, Step.EDIT)));

        assertThat(fileNames(singleRunDir()))
                .containsExactlyInAnyOrder("01_source.md", "04_translated.md", "report.md");
        assertThat(report.skippedRefineSteps()).anyMatch(s -> s.startsWith("edit: only available"));
        assertThat(report.render()).contains("### 5. edit — skipped").contains("Only available for the target language VI");
    }

    @Test
    void shouldStopBeforeRefineTargetWhenTheEditFailsTheGate() throws IOException {
        var source = dir.resolve("chuong14.md");
        Files.writeString(source, "# 清热剂\n\n本方用于气分热盛证。\n");
        var llm = mock(MarkdownChat.class);
        when(llm.chat(anyString(), anyString())).thenAnswer(i -> {
            String system = i.getArgument(0);
            String user = i.getArgument(1);
            if (system.contains("biên tập viên")) {
                return user + "\n\n## Thêm tiêu đề";
            }
            return user.replace("清热剂", "Bài thuốc thanh nhiệt")
                    .replace("本方用于气分热盛证。", "Dùng cho chứng khí phận nhiệt thịnh.");
        });
        var service = service(llm);

        assertThatThrownBy(() -> service.run(
                        request(source, LanguageCode.VI, EnumSet.of(Step.TRANSLATE, Step.EDIT, Step.REFINE_TARGET))))
                .isInstanceOf(QualityGateException.class)
                .hasMessageContaining("'edit'");

        assertThat(fileNames(singleRunDir()))
                .containsExactlyInAnyOrder("01_source.md", "04_translated.md", "05_edited.md", "report.md");
    }

    @Test
    void shouldRunOnlyTheSelectedStepsOnAnExistingMarkdown() throws IOException {
        var source = dir.resolve("chuong3.md");
        Files.writeString(source, "# 清热剂\n\n本方用于气分热盛证。\n");
        var service = lenient(llm(text -> text, text -> text.replace("清热剂", "Bài thuốc thanh nhiệt")));

        var report = service.run(request(source, LanguageCode.VI, EnumSet.of(Step.TRANSLATE)));

        var runDir = singleRunDir();
        assertThat(fileNames(runDir)).containsExactlyInAnyOrder("01_source.md", "04_translated.md", "report.md");
        assertThat(report.outputDocx()).isNull();
        assertThat(report.finalFile()).hasFileName("04_translated.md");
        assertThat(report.render()).contains("none (export not selected)").contains("OCR fixes: step not run");
    }

    @Test
    void shouldExportTheLastWrittenMarkdown() throws IOException {
        var source = dir.resolve("chuong4.md");
        Files.writeString(source, "# 请热剂\n\n本方用于气分热盛证。\n");
        var service = service(llm(text -> text.replace("请", "清"), text -> text));

        var report = service.run(request(source, LanguageCode.VI, EnumSet.of(Step.OCR, Step.EXPORT)));

        assertThat(fileNames(singleRunDir()))
                .containsExactlyInAnyOrder("01_source.md", "02_ocr.md", "07_chuong4_tieng_Viet.docx", "report.md");
        assertThat(report.outputDocx()).exists();
    }

    @Test
    void shouldReportMissingChineseSourceWhenRefiningTheTargetAlone() throws IOException {
        var source = dir.resolve("chuong5.md");
        Files.writeString(source, "# Chương 3\n\n#### Thành phần\n\nSài hồ nửa cân\n");
        var service = service(llm(text -> text, text -> text));

        var report = service.run(request(source, LanguageCode.VI, EnumSet.of(Step.REFINE_TARGET)));

        assertThat(fileNames(singleRunDir())).contains("06_refined.md");
        assertThat(report.skippedRefineSteps()).anyMatch(s -> s.contains("no Chinese source in this run"));
    }

    @Test
    void shouldTranslateNumberedListItems() throws IOException {
        var source = dir.resolve("chuong6.md");
        Files.writeString(source, "# 标题\n\n1. 伤寒少阳证。\n\n2. 往来寒热。\n");
        var service = lenient(llm(
                text -> text,
                text -> text.replace("伤寒少阳证", "Chứng thiếu dương").replace("往来寒热", "Hàn nhiệt vãng lai")));

        service.run(request(source, LanguageCode.VI, EnumSet.of(Step.OCR, Step.TRANSLATE)));

        assertThat(singleRunDir().resolve("04_translated.md"))
                .content()
                .contains("1. Chứng thiếu dương")
                .contains("2. Hàn nhiệt vãng lai");
    }

    @Test
    void shouldKeepTheOriginalSectionAndReportWhenTheLlmFails() throws IOException {
        var source = dir.resolve("chuong7.md");
        Files.writeString(source, "# 标题\n\n本方用于气分热盛证。\n");
        var service = lenient(llm(text -> text, text -> {
            throw new IllegalStateException("boom");
        }));

        var report = service.run(request(source, LanguageCode.VI, EnumSet.of(Step.TRANSLATE)));

        assertThat(singleRunDir().resolve("04_translated.md")).content().contains("本方用于气分热盛证");
        assertThat(report.failedSections()).isEqualTo(1);
        assertThat(report.render()).contains("failed (kept as the original): 1").contains("boom");
    }

    @Test
    void shouldWriteAReportEvenWhenTheRunFails() throws IOException {
        var source = dir.resolve("chuong8.pdf");
        Files.writeString(source, "not a supported source");
        var service = service(llm(text -> text, text -> text));

        assertThatThrownBy(() -> service.run(request(source, LanguageCode.VI, EnumSet.copyOf(Step.all()))))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(singleRunDir().resolve("report.md"))
                .content()
                .contains("Output: none (the run failed)")
                .contains("Error: Unsupported source format")
                .contains("### 1. load — failed")
                .contains("### 2. ocr — not run (the run stopped earlier)");
    }

    @Test
    void shouldCreateSeparateRunFolderPerRunAndKeepEarlierFiles() throws IOException {
        var source = dir.resolve("chuong9.md");
        Files.writeString(source, "本方用于气分热盛证。\n");
        var service = lenient(llm(text -> text, text -> text));
        var request = request(source, LanguageCode.VI, EnumSet.of(Step.OCR, Step.TRANSLATE));

        var first = service.run(request);
        var firstOcr = Files.readString(first.runDir().resolve("02_ocr.md"));
        var second = service.run(request);

        assertThat(second.runDir()).isNotEqualTo(first.runDir()).exists();
        assertThat(first.runDir().resolve("02_ocr.md")).content().isEqualTo(firstOcr);
    }

    @Test
    void shouldStopBeforeTheNextStepWhenTranslationFailsTheGate() throws IOException {
        var source = dir.resolve("chuong10.md");
        Files.writeString(source, "# 标题\n\n本方用于气分热盛证。\n");
        var service = service(llm(text -> text, text -> {
            throw new IllegalStateException("boom");
        }));

        assertThatThrownBy(() -> service.run(
                        request(source, LanguageCode.VI, EnumSet.of(Step.OCR, Step.TRANSLATE, Step.EXPORT))))
                .isInstanceOf(QualityGateException.class)
                .hasMessageContaining("'translate'");

        var runDir = singleRunDir();
        assertThat(fileNames(runDir))
                .containsExactlyInAnyOrder("01_source.md", "02_ocr.md", "04_translated.md", "report.md");
        assertThat(runDir.resolve("report.md"))
                .content()
                .contains("FAIL translate")
                .contains("PASS ocr");
    }

    @Test
    void shouldStopAtOcrWhenTheLlmReplacesTheChineseText() throws IOException {
        var source = dir.resolve("chuong11.md");
        Files.writeString(source, "# 清热剂\n\n本方用于气分热盛证。\n");
        var llm = llm(text -> "Day la ban dich tieng Viet", text -> text);
        var service = service(llm);

        assertThatThrownBy(() -> service.run(request(source, LanguageCode.VI, EnumSet.copyOf(Step.all()))))
                .isInstanceOf(QualityGateException.class)
                .hasMessageContaining("'ocr'");

        assertThat(fileNames(singleRunDir())).containsExactlyInAnyOrder("01_source.md", "02_ocr.md", "report.md");
    }

    private Path singleRunDir() throws IOException {
        try (Stream<Path> children = Files.list(dir.resolve("out"))) {
            var runDirs = children.toList();
            assertThat(runDirs).hasSize(1);
            return runDirs.get(0);
        }
    }

    private static List<String> fileNames(Path runDir) throws IOException {
        try (Stream<Path> children = Files.list(runDir)) {
            return children.map(p -> p.getFileName().toString()).toList();
        }
    }

    @Test
    void shouldCreateOnlyTheChatsOfTheSelectedSteps() throws IOException {
        var source = dir.resolve("book.md");
        Files.writeString(source, "# 章\n\n甲药\n");
        var requested = new java.util.concurrent.CopyOnWriteArrayList<Step>();
        var chats = StepChats.of(step -> {
            requested.add(step);
            return StepChat.of((system, user) -> user.replace("甲", "A"));
        });
        var defaults = GatePolicy.defaults();
        var service = new MedDocTranslationService(
                chats,
                new GatePolicy(
                        defaults.minSourceHan(),
                        defaults.minOcrHanRetention(),
                        defaults.minRefineSourceHanRetention(),
                        Integer.MAX_VALUE,
                        defaults.minBlockRatio(),
                        defaults.maxBlockRatio(),
                        1.0,
                        defaults.maxRefineTargetLlmFailures()));

        var report = service.run(request(source, LanguageCode.VI, EnumSet.of(Step.TRANSLATE)));

        assertThat(report.runDir().resolve("04_translated.md")).exists();
        assertThat(requested).containsExactly(Step.TRANSLATE);
    }

    private static GatePolicy lenientPolicy() {
        var defaults = GatePolicy.defaults();
        return new GatePolicy(
                defaults.minSourceHan(),
                defaults.minOcrHanRetention(),
                defaults.minRefineSourceHanRetention(),
                Integer.MAX_VALUE,
                defaults.minBlockRatio(),
                defaults.maxBlockRatio(),
                1.0,
                defaults.maxRefineTargetLlmFailures());
    }

    private static StepChats recording(List<Step> requested, boolean claudeTranslate) {
        return StepChats.of(step -> {
            requested.add(step);
            return StepChat.of((system, user) -> user, step == Step.TRANSLATE && claudeTranslate);
        });
    }

    @Test
    void shouldSkipTheEditStepWhenTheTranslationUsesClaude() throws IOException {
        var source = dir.resolve("claude.md");
        Files.writeString(source, "# 章\n\n甲药\n");
        var requested = new java.util.concurrent.CopyOnWriteArrayList<Step>();
        var service = new MedDocTranslationService(recording(requested, true), lenientPolicy());

        var report = service.run(request(source, LanguageCode.VI, EnumSet.of(Step.TRANSLATE, Step.EDIT)));

        assertThat(requested).containsExactly(Step.TRANSLATE);
        assertThat(fileNames(singleRunDir())).doesNotContain("05_edited.md");
        assertThat(report.skippedRefineSteps()).anyMatch(s -> s.contains("edit: skipped"));
        assertThat(report.render()).contains("Steps: translate").doesNotContain("edit,");
    }

    @Test
    void shouldFailWhenOnlyTheSkippedEditStepWasRequested() throws IOException {
        var source = dir.resolve("claude-only-edit.md");
        Files.writeString(source, "# Chương 1\n\nNội dung\n");
        var service = new MedDocTranslationService(recording(new java.util.ArrayList<>(), true), lenientPolicy());

        assertThatThrownBy(() -> service.run(request(source, LanguageCode.VI, EnumSet.of(Step.EDIT))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nothing is left to run");
    }

    @Test
    void shouldStillRunTheEditStepWhenTheTranslationDoesNotUseClaude() throws IOException {
        var source = dir.resolve("other.md");
        Files.writeString(source, "# Chương 1\n\nNội dung\n");
        var requested = new java.util.concurrent.CopyOnWriteArrayList<Step>();
        var service = new MedDocTranslationService(recording(requested, false), lenientPolicy());

        service.run(request(source, LanguageCode.VI, EnumSet.of(Step.EDIT)));

        assertThat(requested).contains(Step.EDIT);
        assertThat(fileNames(singleRunDir())).contains("05_edited.md");
    }
}
