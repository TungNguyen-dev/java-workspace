package tungnn.tutor.java.tool.meddoc.core.pipeline;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;
import tungnn.tutor.java.tool.meddoc.core.Step;
import tungnn.tutor.java.tool.meddoc.core.gate.GatePolicy;
import tungnn.tutor.java.tool.meddoc.core.ocr.OcrFix;

public final class PipelineState {

    private final Path runDir;
    private final String baseName;
    private final LanguageCode target;
    private final Set<Step> selected;
    private final GatePolicy gatePolicy;
    private final List<String> skipped;
    private MarkdownDocument document;
    private MarkdownDocument chineseSource;
    private Path current;
    private Path docx;
    private List<OcrFix> fixes = List.of();

    public PipelineState(
            Path runDir,
            String baseName,
            LanguageCode target,
            Set<Step> selected,
            GatePolicy gatePolicy,
            List<String> skipped) {
        this.runDir = Objects.requireNonNull(runDir, "runDir must not be null");
        this.baseName = Objects.requireNonNull(baseName, "baseName must not be null");
        this.target = Objects.requireNonNull(target, "target must not be null");
        this.selected = Set.copyOf(selected);
        this.gatePolicy = Objects.requireNonNull(gatePolicy, "gatePolicy must not be null");
        this.skipped = Objects.requireNonNull(skipped, "skipped must not be null");
    }

    public boolean runs(Step step) {
        return selected.contains(step);
    }

    public Path runDir() {
        return runDir;
    }

    public String baseName() {
        return baseName;
    }

    public LanguageCode target() {
        return target;
    }

    public GatePolicy gatePolicy() {
        return gatePolicy;
    }

    public List<String> skipped() {
        return skipped;
    }

    public MarkdownDocument document() {
        return document;
    }

    public void setDocument(MarkdownDocument document) {
        this.document = document;
    }

    public MarkdownDocument chineseSource() {
        return chineseSource;
    }

    public void setChineseSource(MarkdownDocument chineseSource) {
        this.chineseSource = chineseSource;
    }

    public Path current() {
        return current;
    }

    public void setCurrent(Path current) {
        this.current = current;
    }

    public Path docx() {
        return docx;
    }

    public void setDocx(Path docx) {
        this.docx = docx;
    }

    public List<OcrFix> fixes() {
        return fixes;
    }

    public void setFixes(List<OcrFix> fixes) {
        this.fixes = List.copyOf(fixes);
    }

    public Path write(String fileName, MarkdownDocument result) throws IOException {
        var path = StepFilesUtils.write(runDir, fileName, result);
        document = result;
        current = path;
        return path;
    }
}
