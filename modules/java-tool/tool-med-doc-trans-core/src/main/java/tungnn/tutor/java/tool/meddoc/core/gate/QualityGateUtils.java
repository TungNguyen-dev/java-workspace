package tungnn.tutor.java.tool.meddoc.core.gate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import tungnn.tutor.java.infra.document.adapter.markdown.MarkdownDocumentUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Heading;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Paragraph;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;
import tungnn.tutor.java.infra.document.converter.DocxMarkdownConverterUtils;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;
import tungnn.tutor.java.tool.meddoc.core.Step;
import tungnn.tutor.java.tool.meddoc.core.inspect.DoseCheckUtils;
import tungnn.tutor.java.tool.meddoc.core.inspect.SuspiciousTextUtils;
import tungnn.tutor.java.tool.meddoc.core.markdown.MarkdownActions;

public final class QualityGateUtils {

    public static final String LOAD = "load";
    private static final int MAX_SECTION_LEVEL = 3;

    private QualityGateUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static GateResult load(MarkdownDocument doc, boolean needsChinese, GatePolicy policy) {
        var problems = new ArrayList<String>();
        int text = textBlocks(doc);
        int han = hanCount(doc);
        if (text == 0) {
            problems.add("the source has no text");
        }
        if (needsChinese && han < policy.minSourceHan()) {
            problems.add("no Chinese text found in the source (expected at least %d characters)"
                    .formatted(policy.minSourceHan()));
        }
        return new GateResult(LOAD, problems, List.of("text blocks: " + text, "Han: " + han));
    }

    public static GateResult checkDoses(DoseCheckUtils.Result result, GatePolicy policy) {
        var problems = new ArrayList<String>();
        if (result.mismatches().size() > policy.maxDoseMismatches()) {
            var first = result.mismatches().getFirst();
            problems.add("%d sections have doses that differ from the source (max %d), first: %s"
                    .formatted(result.mismatches().size(), policy.maxDoseMismatches(), first.section()));
        }
        return new GateResult(
                Step.CHECK_DOSES.cliName(),
                problems,
                List.of("sections compared: " + result.sections(), "mismatches: " + result.mismatches().size()));
    }

    public static GateResult glossary(int terms, int candidates) {
        return new GateResult(
                Step.GLOSSARY.cliName(), List.of(), List.of("terms: " + terms, "candidates: " + candidates));
    }

    public static GateResult scan(Step step, int suspects) {
        return new GateResult(step.cliName(), List.of(), List.of("suspects: " + suspects));
    }

    public static GateResult fixOcr(MarkdownDocument before, MarkdownDocument after, GatePolicy policy) {
        var problems = new ArrayList<String>();
        int textBefore = textBlocks(before);
        int textAfter = textBlocks(after);
        if (textAfter < textBefore) {
            problems.add("text blocks dropped from %d to %d".formatted(textBefore, textAfter));
        }
        double retention = ratio(hanCount(after), hanCount(before));
        if (retention < policy.minFixOcrHanRetention()) {
            problems.add("Han retention %s is below %s".formatted(fmt(retention), fmt(policy.minFixOcrHanRetention())));
        }
        return new GateResult(
                Step.FIX_OCR.cliName(), problems, List.of("Han retention: " + fmt(retention), "text blocks: " + textAfter));
    }

    public static GateResult structureSource(MarkdownDocument before, MarkdownDocument after, GatePolicy policy) {
        return structureSource(before, after, 0, policy);
    }

    public static GateResult structureSource(
            MarkdownDocument before, MarkdownDocument after, int removedHan, GatePolicy policy) {
        var problems = new ArrayList<String>();
        int headings = headingCount(after);
        if (headings == 0) {
            problems.add("no heading of level 1-3 found, the document was not structured");
        }
        double retention = ratio(hanCount(after), Math.max(0, hanCount(before) - removedHan));
        if (retention < policy.minStructureSourceHanRetention()) {
            problems.add("Han retention %s is below %s"
                    .formatted(fmt(retention), fmt(policy.minStructureSourceHanRetention())));
        }
        var metrics = new ArrayList<String>();
        metrics.add("Han retention: " + fmt(retention));
        metrics.add("headings: " + headings);
        if (removedHan > 0) {
            metrics.add("removed Han: " + removedHan);
        }
        return new GateResult(Step.STRUCTURE_SOURCE.cliName(), problems, metrics);
    }

    public static GateResult translate(
            MarkdownDocument before,
            MarkdownDocument after,
            LanguageCode target,
            int failedSections,
            List<String> headingWarnings,
            boolean relabelFollows,
            GatePolicy policy) {
        var problems = new ArrayList<String>();
        if (failedSections > policy.maxFailedSections()) {
            problems.add(
                    "%d sections failed to translate (max %d)".formatted(failedSections, policy.maxFailedSections()));
        }
        var normalized = MarkdownActions.orderedListToParagraphs(true).apply(before);
        double blockRatio = ratio(after.blocks().size(), normalized.blocks().size());
        if (blockRatio < policy.minBlockRatio() || blockRatio > policy.maxBlockRatio()) {
            problems.add("block ratio %s is outside %s-%s"
                    .formatted(fmt(blockRatio), fmt(policy.minBlockRatio()), fmt(policy.maxBlockRatio())));
        }
        var metrics = new ArrayList<String>();
        metrics.add("block ratio: " + fmt(blockRatio));
        untranslated(after, target, policy, problems, metrics);
        if (relabelFollows && !headingWarnings.isEmpty()) {
            problems.add(
                    "heading levels changed, section labels cannot be matched: " + String.join(" | ", headingWarnings));
        }
        return new GateResult(Step.TRANSLATE.cliName(), problems, metrics);
    }

    public static GateResult polishTarget(
            MarkdownDocument before,
            MarkdownDocument after,
            LanguageCode target,
            int failedSections,
            List<String> headingWarnings,
            GatePolicy policy) {
        var problems = new ArrayList<String>();
        if (failedSections > policy.maxFailedSections()) {
            problems.add("%d sections failed to polish (max %d)".formatted(failedSections, policy.maxFailedSections()));
        }
        double blockRatio = ratio(after.blocks().size(), before.blocks().size());
        if (blockRatio < policy.minBlockRatio() || blockRatio > policy.maxBlockRatio()) {
            problems.add("block ratio %s is outside %s-%s"
                    .formatted(fmt(blockRatio), fmt(policy.minBlockRatio()), fmt(policy.maxBlockRatio())));
        }
        int headingsBefore = headingCount(before);
        int headingsAfter = headingCount(after);
        if (headingsAfter != headingsBefore) {
            problems.add("headings of level 1-3 changed from %d to %d".formatted(headingsBefore, headingsAfter));
        }
        if (!headingWarnings.isEmpty()) {
            problems.add(
                    "heading levels changed, section labels cannot be matched: " + String.join(" | ", headingWarnings));
        }
        var metrics = new ArrayList<String>();
        metrics.add("block ratio: " + fmt(blockRatio));
        untranslated(after, target, policy, problems, metrics);
        return new GateResult(Step.POLISH_TARGET.cliName(), problems, metrics);
    }

    public static GateResult formatTarget(
            MarkdownDocument before, MarkdownDocument after, LanguageCode target, int llmFailures, GatePolicy policy) {
        var problems = new ArrayList<String>();
        int headingsBefore = headingCount(before);
        int headingsAfter = headingCount(after);
        if (headingsAfter < headingsBefore) {
            problems.add("headings of level 1-3 dropped from %d to %d".formatted(headingsBefore, headingsAfter));
        }
        if (llmFailures > policy.maxFormatTargetLlmFailures()) {
            problems.add(
                    "%d LLM format calls failed (max %d)".formatted(llmFailures, policy.maxFormatTargetLlmFailures()));
        }
        var metrics = new ArrayList<String>();
        metrics.add("headings: " + headingsAfter);
        untranslated(after, target, policy, problems, metrics);
        return new GateResult(Step.FORMAT_TARGET.cliName(), problems, metrics);
    }

    public static GateResult export(Path docx) {
        var problems = new ArrayList<String>();
        var metrics = new ArrayList<String>();
        try {
            if (docx == null || !Files.isRegularFile(docx) || Files.size(docx) == 0) {
                problems.add("the DOCX was not written or is empty");
            } else {
                metrics.add("size: " + Files.size(docx) + " bytes");
                int text = textBlocks(MarkdownDocumentUtils.parse(DocxMarkdownConverterUtils.toMarkdown(docx)));
                metrics.add("text blocks: " + text);
                if (text == 0) {
                    problems.add("the DOCX has no text when read back");
                }
            }
        } catch (IOException | RuntimeException e) {
            problems.add("the DOCX cannot be read back: " + e.getMessage());
        }
        return new GateResult(Step.EXPORT.cliName(), problems, metrics);
    }

    public static int hanCount(MarkdownDocument doc) {
        int count = 0;
        for (var block : doc.blocks()) {
            count += (int) MarkdownActions.textOf(block)
                    .codePoints()
                    .filter(cp -> Character.UnicodeScript.of(cp) == Character.UnicodeScript.HAN)
                    .count();
        }
        return count;
    }

    public static int textBlocks(MarkdownDocument doc) {
        return (int) doc.blocks().stream().filter(QualityGateUtils::isText).count();
    }

    public static int headingCount(MarkdownDocument doc) {
        return (int) doc.blocks().stream()
                .filter(b -> b instanceof Heading h && h.level() <= MAX_SECTION_LEVEL)
                .count();
    }

    public static int blocksWithHan(MarkdownDocument doc) {
        return (int) doc.blocks().stream()
                .filter(QualityGateUtils::isText)
                .filter(b -> SuspiciousTextUtils.containsHan(MarkdownActions.textOf(b)))
                .count();
    }

    private static void untranslated(
            MarkdownDocument after,
            LanguageCode target,
            GatePolicy policy,
            List<String> problems,
            List<String> metrics) {
        if (target == LanguageCode.ZH) {
            return;
        }
        int text = textBlocks(after);
        double ratio = text == 0 ? 0 : (double) blocksWithHan(after) / text;
        metrics.add("untranslated ratio: " + fmt(ratio));
        if (ratio > policy.maxUntranslatedRatio()) {
            problems.add("%s of the blocks still contain Chinese (max %s)"
                    .formatted(fmt(ratio), fmt(policy.maxUntranslatedRatio())));
        }
    }

    private static boolean isText(MarkdownBlock block) {
        return (block instanceof Heading || block instanceof Paragraph)
                && !MarkdownActions.textOf(block).isBlank();
    }

    private static double ratio(int numerator, int denominator) {
        return denominator == 0 ? 1.0 : (double) numerator / denominator;
    }

    private static String fmt(double value) {
        return String.format(Locale.ROOT, "%.3f", value);
    }
}
