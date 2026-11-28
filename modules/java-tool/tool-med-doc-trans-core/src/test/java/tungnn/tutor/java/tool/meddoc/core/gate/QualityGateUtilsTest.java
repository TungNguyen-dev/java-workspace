package tungnn.tutor.java.tool.meddoc.core.gate;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.infra.document.adapter.markdown.MarkdownDocumentUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;
import tungnn.tutor.java.tool.meddoc.core.markdown.MarkdownActions;

class QualityGateUtilsTest {

    private static final GatePolicy POLICY = GatePolicy.defaults();

    private static MarkdownDocument doc(String markdown) {
        return MarkdownDocumentUtils.parse(markdown);
    }

    @Test
    void loadShouldFailWithoutChineseWhenAChineseStepWillRun() {
        assertThat(QualityGateUtils.load(doc("Hello world"), true, POLICY).passed())
                .isFalse();
        assertThat(QualityGateUtils.load(doc("Hello world"), false, POLICY).passed())
                .isTrue();
        assertThat(QualityGateUtils.load(doc("本方用于气分热盛证。"), true, POLICY).passed())
                .isTrue();
    }

    @Test
    void ocrShouldFailWhenHanCharactersAreLost() {
        var before = doc("# 清热剂\n\n本方用于气分热盛证。");
        assertThat(QualityGateUtils.fixOcr(before, before, POLICY).passed()).isTrue();
        assertThat(QualityGateUtils.fixOcr(before, doc("# Thanh nhiet\n\nBan dich"), POLICY)
                        .passed())
                .isFalse();
    }

    @Test
    void refineSourceShouldFailWithoutAnyHeading() {
        var before = doc("本方用于气分热盛证。\n\n清热剂适用于热证。");
        assertThat(QualityGateUtils.structureSource(before, before, POLICY).passed())
                .isFalse();
        assertThat(QualityGateUtils.structureSource(before, doc("# 清热剂\n\n本方用于气分热盛证。\n\n清热剂适用于热证。"), POLICY)
                        .passed())
                .isTrue();
    }

    @Test
    void refineSourceShouldNotCountRemovedSectionsAsLost() {
        var before = doc("# 清热剂\n\n本方用于气分热盛证。\n\n#### 病案\n\n某男发热三日服药而愈病案内容很长很长很长");
        var after = doc("# 清热剂\n\n本方用于气分热盛证。");
        int removed = QualityGateUtils.hanCount(before) - QualityGateUtils.hanCount(after);

        assertThat(QualityGateUtils.structureSource(before, after, POLICY).passed())
                .isFalse();
        assertThat(QualityGateUtils.structureSource(before, after, removed, POLICY).passed())
                .isTrue();
        assertThat(QualityGateUtils.structureSource(before, doc("# 清热剂\n\n本方"), removed, POLICY)
                        .passed())
                .isFalse();
    }

    @Test
    void editShouldKeepTheStructureAndNotBringChineseBack() {
        var before = doc("### Bạch hổ thang\n\nNhiệt thịnh cho nên khát nước.\n\nCách dùng thuốc.");
        var same = doc("### Bạch hổ thang\n\nNhiệt thịnh nên khát nước.\n\nCách dùng thuốc.");

        assertThat(QualityGateUtils.polishTarget(before, same, LanguageCode.VI, 0, List.of(), POLICY)
                        .passed())
                .isTrue();
        assertThat(QualityGateUtils.polishTarget(before, same, LanguageCode.VI, 1, List.of(), POLICY)
                        .passed())
                .isFalse();
        assertThat(QualityGateUtils.polishTarget(
                                before, doc("### Bạch hổ thang\n\nNhiệt thịnh."), LanguageCode.VI, 0, List.of(), POLICY)
                        .passed())
                .isFalse();
        assertThat(QualityGateUtils.polishTarget(
                                before,
                                doc("### Bạch hổ thang\n\n## Mục\n\nNhiệt thịnh nên khát nước.\n\nCách dùng thuốc."),
                                LanguageCode.VI,
                                0,
                                List.of(),
                                POLICY)
                        .passed())
                .isFalse();
        assertThat(QualityGateUtils.polishTarget(
                                before,
                                doc("### Bạch hổ thang\n\n白虎汤\n\nCách dùng thuốc."),
                                LanguageCode.VI,
                                0,
                                List.of(),
                                POLICY)
                        .passed())
                .isFalse();
        assertThat(QualityGateUtils.polishTarget(before, same, LanguageCode.VI, 0, List.of("levels changed"), POLICY)
                        .passed())
                .isFalse();
    }

    @Test
    void translateShouldFailOnFailedSectionsOrLeftoverChinese() {
        var before = doc("# 清热剂\n\n本方用于气分热盛证。");
        var good = doc("# Bài thuốc thanh nhiệt\n\nBài này dùng cho chứng khí phận nhiệt thịnh.");
        assertThat(QualityGateUtils.translate(before, good, LanguageCode.VI, 0, List.of(), false, POLICY)
                        .passed())
                .isTrue();
        assertThat(QualityGateUtils.translate(before, good, LanguageCode.VI, 1, List.of(), false, POLICY)
                        .passed())
                .isFalse();
        assertThat(QualityGateUtils.translate(before, before, LanguageCode.VI, 0, List.of(), false, POLICY)
                        .passed())
                .isFalse();
        assertThat(QualityGateUtils.translate(before, before, LanguageCode.ZH, 0, List.of(), false, POLICY)
                        .passed())
                .isTrue();
    }

    @Test
    void translateShouldOnlyBlockOnHeadingWarningsWhenRelabelFollows() {
        var before = doc("# 清热剂\n\n本方用于气分热盛证。");
        var good = doc("# Bài thuốc thanh nhiệt\n\nBài này dùng cho chứng khí phận nhiệt thịnh.");
        var warnings = List.of("section 1: heading levels changed");
        assertThat(QualityGateUtils.translate(before, good, LanguageCode.VI, 0, warnings, false, POLICY)
                        .passed())
                .isTrue();
        assertThat(QualityGateUtils.translate(before, good, LanguageCode.VI, 0, warnings, true, POLICY)
                        .passed())
                .isFalse();
    }

    @Test
    void translateShouldNormalizeListsBeforeComparingBlockCounts() {
        var before = doc("# 标题\n\n1. 伤寒少阳证。\n\n2. 往来寒热。\n\n3. 口苦咽干。");
        var after = MarkdownActions.orderedListToParagraphs(true)
                .apply(doc("# Tiêu đề\n\n1. Chứng thiếu dương.\n\n2. Hàn nhiệt vãng lai.\n\n3. Miệng đắng họng khô."));
        assertThat(QualityGateUtils.translate(before, after, LanguageCode.VI, 0, List.of(), false, POLICY)
                        .passed())
                .isTrue();
    }

    @Test
    void refineTargetShouldFailWhenHeadingsDropOrLlmCallsFailed() {
        var before = doc("# Chương\n\n## Mục\n\nNội dung");
        assertThat(QualityGateUtils.formatTarget(before, before, LanguageCode.VI, 0, POLICY)
                        .passed())
                .isTrue();
        assertThat(QualityGateUtils.formatTarget(before, doc("# Chương\n\nNội dung"), LanguageCode.VI, 0, POLICY)
                        .passed())
                .isFalse();
        assertThat(QualityGateUtils.formatTarget(before, before, LanguageCode.VI, 1, POLICY)
                        .passed())
                .isFalse();
    }

    @Test
    void exportShouldFailForAMissingFile() {
        assertThat(QualityGateUtils.export(java.nio.file.Path.of("does-not-exist.docx"))
                        .passed())
                .isFalse();
    }
}
