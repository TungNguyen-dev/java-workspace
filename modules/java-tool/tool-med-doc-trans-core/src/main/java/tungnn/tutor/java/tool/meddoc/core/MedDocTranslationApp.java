package tungnn.tutor.java.tool.meddoc.core;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import tungnn.tutor.java.ai.config.LlmProvider;
import tungnn.tutor.java.ai.util.LlmClientUtils;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;
import tungnn.tutor.java.tool.meddoc.core.gate.QualityGateException;
import tungnn.tutor.java.tool.meddoc.core.service.MedDocTranslationRequest;
import tungnn.tutor.java.tool.meddoc.core.service.MedDocTranslationService;
import tungnn.tutor.java.tool.meddoc.core.service.Step;

public class MedDocTranslationApp {

    private static final String ENV_PROVIDER = "DOCTRANS_TRANSLATOR_PROVIDER";
    private static final String USAGE =
            "Usage: MedDocTranslationApp <input.docx|md|txt> [--lang VI] [--out dir] [--steps %s] [--no-refine]"
                    .formatted(Step.validNames().replace(", ", ","));

    static void main(String[] args) throws IOException {
        MedDocTranslationRequest request;
        try {
            request = parse(args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.err.println(USAGE);
            System.exit(1);
            return;
        }

        var providerName = System.getenv(ENV_PROVIDER);
        var provider =
                providerName == null || providerName.isBlank() ? LlmProvider.DEEPSEEK : LlmProvider.from(providerName);
        var service = new MedDocTranslationService(LlmClientUtils.fromEnv(provider));
        try {
            var report = service.run(request);
            System.out.println(report.render());
        } catch (QualityGateException e) {
            if (e.report() != null) {
                System.out.println(e.report());
            }
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    static MedDocTranslationRequest parse(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("Missing input file");
        }
        var source = Path.of(args[0]);
        var lang = LanguageCode.VI;
        var out = source.toAbsolutePath().getParent();
        Set<Step> steps = Step.all();
        var refine = true;
        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "--lang" -> lang = parseLanguage(value(args, ++i, "--lang"));
                case "--out" -> out = Path.of(value(args, ++i, "--out"));
                case "--steps" -> steps = Step.parse(value(args, ++i, "--steps"));
                case "--no-refine" -> refine = false;
                default -> throw new IllegalArgumentException("Unknown option: " + args[i]);
            }
        }
        if (!refine) {
            steps.remove(Step.REFINE_SOURCE);
            steps.remove(Step.REFINE_TARGET);
            if (steps.isEmpty()) {
                throw new IllegalArgumentException("--no-refine leaves no step to run");
            }
        }
        return new MedDocTranslationRequest(source, lang, out, steps);
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
