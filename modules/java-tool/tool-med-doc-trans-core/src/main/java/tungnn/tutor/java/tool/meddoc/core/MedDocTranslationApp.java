package tungnn.tutor.java.tool.meddoc.core;

import java.io.IOException;
import java.nio.file.Path;
import tungnn.tutor.java.ai.config.LlmProvider;
import tungnn.tutor.java.ai.util.LlmClientUtils;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;

public class MedDocTranslationApp {

    private static final String ENV_PROVIDER = "DOCTRANS_TRANSLATOR_PROVIDER";

    static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage: MedDocTranslationApp <input.docx|md|txt> [--lang VI] [--out dir] [--no-refine]");
            System.exit(1);
        }
        var source = Path.of(args[0]);
        var lang = LanguageCode.VI;
        var out = source.toAbsolutePath().getParent();
        var refine = true;
        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "--lang" -> lang = LanguageCode.valueOf(args[++i].toUpperCase());
                case "--out" -> out = Path.of(args[++i]);
                case "--no-refine" -> refine = false;
                default -> throw new IllegalArgumentException("Unknown option: " + args[i]);
            }
        }

        var providerName = System.getenv(ENV_PROVIDER);
        var provider = providerName == null || providerName.isBlank()
                ? LlmProvider.DEEPSEEK
                : LlmProvider.from(providerName);
        var service = new MedDocTranslationService(LlmClientUtils.fromEnv(provider));
        var report = service.run(new MedDocTranslationRequest(source, lang, out, out.resolve("memory"), refine));
        System.out.println(report.render());
    }
}
