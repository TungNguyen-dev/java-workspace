package tungnn.tutor.java.tool.hub.plugin.meddoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.pf4j.Extension;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;
import tungnn.tutor.java.tool.hub.api.ParamSpec;
import tungnn.tutor.java.tool.hub.api.ToolContext;
import tungnn.tutor.java.tool.hub.api.ToolDescriptor;
import tungnn.tutor.java.tool.hub.api.ToolException;
import tungnn.tutor.java.tool.hub.api.ToolExtension;
import tungnn.tutor.java.tool.hub.api.ToolFile;
import tungnn.tutor.java.tool.hub.api.ToolLayout;
import tungnn.tutor.java.tool.hub.api.ToolResult;
import tungnn.tutor.java.tool.meddoc.core.service.MedDocTranslationRequest;
import tungnn.tutor.java.tool.meddoc.core.service.MedDocTranslationService;
import tungnn.tutor.java.tool.meddoc.core.service.Step;

@Extension
public class MedDocTranslateTool implements ToolExtension {

    private static final String DOCX_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    private static final String MARKDOWN_TYPE = "text/markdown";
    private static final List<String> SUPPORTED = List.of(".docx", ".md", ".markdown", ".txt");
    private static final String LANGUAGE_TYPE = languageType();

    private volatile MedLlmClientFactory factory;

    private static String languageType() {
        StringBuilder sb = new StringBuilder("enum:");
        for (LanguageCode c : LanguageCode.values()) {
            if (sb.length() > 5) sb.append('|');
            sb.append(c.name());
        }
        return sb.toString();
    }

    private static Path dataDir(ToolContext ctx) {
        return ctx != null && ctx.dataDir() != null
                ? ctx.dataDir()
                : Path.of(System.getProperty("java.io.tmpdir"), "med-doc-trans");
    }

    private static String safeFileName(String filename) {
        int slash = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
        String name = filename.substring(slash + 1).replaceAll("[<>:\"/\\\\|?\\x00-\\x1F]", "_");
        return name.isBlank() ? "document" : name;
    }

    private static boolean supported(String filename) {
        String lower = filename.toLowerCase(Locale.ROOT);
        return SUPPORTED.stream().anyMatch(lower::endsWith);
    }

    private static void deleteRecursively(Path dir) {
        if (!Files.exists(dir)) return;
        try (var s = Files.walk(dir)) {
            s.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
        } catch (IOException ignored) {
        }
    }

    private static String rootMessage(Throwable e) {
        Throwable root = e;
        while (root.getCause() != null && root.getCause() != root) root = root.getCause();
        String msg = root.getMessage();
        return root.getClass().getSimpleName() + (msg == null || msg.isBlank() ? "" : ": " + msg);
    }

    @Override
    public void init(ToolContext ctx) {
        factory = new MedLlmClientFactory(dataDir(ctx));
    }

    @Override
    public ToolDescriptor descriptor() {
        return new ToolDescriptor(
                "med-doc-trans",
                "Medical Document Translator",
                "Convert a Dong y textbook (.docx/.md/.txt) to Markdown, fix OCR errors, translate it with an LLM"
                        + " and get a DOCX plus a report of doubtful passages",
                "translation",
                ToolLayout.FILE);
    }

    @Override
    public List<ParamSpec> params() {
        return List.of(
                new ParamSpec("file", "file", true, "Document (.docx, .md, .txt)"),
                new ParamSpec("targetLang", LANGUAGE_TYPE, true, "Translate to", LanguageCode.VI.name()),
                new ParamSpec(
                        "provider",
                        MedLlmClientFactory.providerParamType(),
                        true,
                        "LLM provider",
                        factory().defaultProvider()),
                new ParamSpec("model", "string", false, "Model (empty = default)"),
                new ParamSpec(
                        "refineVietnamese",
                        "boolean",
                        false,
                        "Restructure the source (headings, sections) and reformat Vietnamese output",
                        "true"),
                new ParamSpec(
                        "steps", "string", false, "Steps to run, comma separated (empty = all): " + Step.validNames()));
    }

    private static Set<Step> parseSteps(Object value, boolean refine) throws ToolException {
        try {
            var text = value == null ? "" : value.toString();
            Set<Step> steps = text.isBlank() ? Step.all() : Step.parse(text);
            if (!refine) {
                steps.remove(Step.REFINE_SOURCE);
                steps.remove(Step.REFINE_TARGET);
            }
            if (steps.isEmpty()) {
                throw new IllegalArgumentException("No steps left to run");
            }
            return steps;
        } catch (IllegalArgumentException e) {
            throw new ToolException(e.getMessage(), e);
        }
    }

    @Override
    public ToolResult execute(ToolContext ctx, Map<String, Object> in) throws ToolException {
        if (!(in.get("file") instanceof ToolFile file) || file.filename() == null) {
            throw new ToolException("Please provide the document to translate.");
        }
        if (!supported(file.filename())) {
            throw new ToolException("Unsupported file '" + file.filename() + "'. Supported: .docx, .md, .txt");
        }
        LanguageCode target;
        try {
            target = LanguageCode.valueOf(String.valueOf(in.get("targetLang")).toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ToolException("Unsupported language: " + in.get("targetLang"), e);
        }
        String provider = in.get("provider") == null
                ? factory().defaultProvider()
                : in.get("provider").toString();
        String model = in.get("model") == null ? null : in.get("model").toString();
        boolean refine = Boolean.TRUE.equals(in.get("refineVietnamese"));
        Set<Step> steps = parseSteps(in.get("steps"), refine);

        Path data = dataDir(ctx);
        Path jobDir = data.resolve("jobs").resolve(UUID.randomUUID().toString());
        Thread thread = Thread.currentThread();
        ClassLoader previous = thread.getContextClassLoader();
        thread.setContextClassLoader(MedDocTranslateTool.class.getClassLoader());
        try {
            Files.createDirectories(jobDir);
            Path input = jobDir.resolve(safeFileName(file.filename()));
            Files.write(input, file.content());
            var service = new MedDocTranslationService(factory().create(provider, model));
            var report = service.run(new MedDocTranslationRequest(input, target, data.resolve("runs"), steps));
            var output = report.outputDocx() != null ? report.outputDocx() : report.finalFile();
            var contentType = report.outputDocx() != null ? DOCX_TYPE : MARKDOWN_TYPE;
            return new ToolResult(
                    true,
                    new ToolFile(output.getFileName().toString(), contentType, Files.readAllBytes(output)),
                    report.render());
        } catch (ToolException e) {
            throw e;
        } catch (Exception e) {
            throw new ToolException("Translation failed: " + rootMessage(e), e);
        } catch (LinkageError e) {
            throw new ToolException("Library compatibility error in plugin: " + rootMessage(e), e);
        } finally {
            thread.setContextClassLoader(previous);
            deleteRecursively(jobDir);
        }
    }

    private MedLlmClientFactory factory() {
        MedLlmClientFactory f = factory;
        if (f == null) {
            synchronized (this) {
                f = factory;
                if (f == null) {
                    f = new MedLlmClientFactory(dataDir(null));
                    factory = f;
                }
            }
        }
        return f;
    }
}
