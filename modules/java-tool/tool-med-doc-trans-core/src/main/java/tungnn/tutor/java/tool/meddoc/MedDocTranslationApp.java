package tungnn.tutor.java.tool.meddoc;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;
import tungnn.tutor.java.tool.meddoc.core.Step;
import tungnn.tutor.java.tool.meddoc.core.gate.QualityGateException;
import tungnn.tutor.java.tool.meddoc.llm.ChatSpec;
import tungnn.tutor.java.tool.meddoc.llm.ChatSpecUtils;
import tungnn.tutor.java.tool.meddoc.llm.StepChats;
import tungnn.tutor.java.tool.meddoc.service.MedDocTranslationRequest;
import tungnn.tutor.java.tool.meddoc.service.MedDocTranslationService;

public class MedDocTranslationApp {

    private static final String USAGE =
            "Usage: MedDocTranslationApp <input.docx|md|txt> [--lang VI] [--out dir] [--steps %s] [--no-refine]"
                            .formatted(Step.validNames().replace(", ", ","))
                    + " [--llm <fix-ocr|glossary|translate|polish-target|format-target|default>=<claude-cli[:model][@effort]|provider[:model]>]...";

    record Args(MedDocTranslationRequest request, Map<String, String> llm) {}

    static void main(String[] args) throws IOException {
        Args parsed;
        Map<Step, ChatSpec> specs;
        try {
            parsed = parse(args);
            specs = ChatSpecUtils.resolve(parsed.llm(), System::getenv);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.err.println(USAGE);
            System.exit(1);
            return;
        }

        var service = new MedDocTranslationService(StepChats.of(specs, System::getenv));
        var request = parsed.request();
        try {
            var report = service.run(request);
            System.out.println(report.render());
        } catch (QualityGateException e) {
            if (e.report() != null) {
                System.out.println(e.report());
            }
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    static Args parse(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("Missing input file");
        }
        var source = Path.of(args[0]);
        var lang = LanguageCode.VI;
        var out = source.toAbsolutePath().getParent();
        Set<Step> steps = Step.all();
        var refine = true;
        var llm = new LinkedHashMap<String, String>();
        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "--lang" -> lang = parseLanguage(value(args, ++i, "--lang"));
                case "--out" -> out = Path.of(value(args, ++i, "--out"));
                case "--steps" -> steps = Step.parse(value(args, ++i, "--steps"));
                case "--no-refine" -> refine = false;
                case "--llm" -> addLlm(llm, value(args, ++i, "--llm"));
                default -> throw new IllegalArgumentException("Unknown option: " + args[i]);
            }
        }
        if (!refine) {
            steps.remove(Step.STRUCTURE_SOURCE);
            steps.remove(Step.POLISH_TARGET);
            steps.remove(Step.FORMAT_TARGET);
            if (steps.isEmpty()) {
                throw new IllegalArgumentException("--no-refine leaves no step to run");
            }
        }
        return new Args(new MedDocTranslationRequest(source, lang, out, steps), llm);
    }

    private static void addLlm(Map<String, String> llm, String assignment) {
        var separator = assignment.indexOf('=');
        if (separator < 1) {
            throw new IllegalArgumentException("Option --llm needs <step>=<spec>, got '" + assignment + "'");
        }
        var key = assignment.substring(0, separator).strip().toLowerCase(Locale.ROOT);
        var spec = assignment.substring(separator + 1).strip();
        ChatSpecUtils.checkOverride(key, spec);
        llm.put(key, spec);
    }

    private static String value(String[] args, int index, String option) {
        if (index >= args.length) {
            throw new IllegalArgumentException("Option " + option + " needs a value");
        }
        return args[index];
    }

    private static LanguageCode parseLanguage(String code) {
        try {
            return LanguageCode.valueOf(code.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Unsupported language '" + code + "'. Valid codes: " + Arrays.toString(LanguageCode.values()));
        }
    }
}
