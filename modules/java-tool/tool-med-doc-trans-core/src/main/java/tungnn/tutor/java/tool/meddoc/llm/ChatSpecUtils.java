package tungnn.tutor.java.tool.meddoc.llm;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import tungnn.tutor.java.ai.chat.LlmClientChat;
import tungnn.tutor.java.ai.config.LlmClientProperties;
import tungnn.tutor.java.ai.config.LlmSpec;
import tungnn.tutor.java.ai.util.LlmClientUtils;
import tungnn.tutor.java.tool.meddoc.core.Step;

public final class ChatSpecUtils {

    public static final String DEFAULT_KEY = "default";
    public static final String ENV_DEFAULT = "DOCTRANS_LLM_DEFAULT";
    public static final String ENV_STEP_PREFIX = "DOCTRANS_LLM_";
    public static final String ENV_LEGACY_PROVIDER = "DOCTRANS_TRANSLATOR_PROVIDER";
    public static final int DEFAULT_CLAUDE_CLI_CONCURRENCY = 2;
    public static final String CLAUDE_CLI_CONCURRENCY_ENV = "DOCTRANS_CLAUDE_CLI_CONCURRENCY";
    private static final String FALLBACK = "deepseek";

    private ChatSpecUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void checkOverride(String key, String spec) {
        if (!DEFAULT_KEY.equals(key)) {
            var step = Step.byCliName(key);
            if (!step.usesLlm()) {
                throw new IllegalArgumentException("Step '" + key + "' does not use an LLM");
            }
        }
        LlmSpec.parse(spec);
    }

    public static Map<Step, LlmSpec> resolve(Map<String, String> overrides, UnaryOperator<String> env) {
        overrides.forEach(ChatSpecUtils::checkOverride);
        var defaultSpec = LlmSpec.parse(firstNonBlank(
                overrides.get(DEFAULT_KEY), env.apply(ENV_DEFAULT), env.apply(ENV_LEGACY_PROVIDER), FALLBACK));
        var specs = new EnumMap<Step, LlmSpec>(Step.class);
        for (var step : Step.values()) {
            if (step.usesLlm()) {
                var configured =
                        firstNonBlank(overrides.get(step.cliName()), env.apply(ENV_STEP_PREFIX + step.envName()), null);
                specs.put(step, configured == null ? defaultSpec : LlmSpec.parse(configured));
            }
        }
        return specs;
    }

    public static StepChat create(LlmSpec spec, UnaryOperator<String> env) {
        var client = LlmClientUtils.create(spec, env);
        var chat = new LlmClientChat(client);
        var concurrency = spec.isClaudeCli() ? claudeCliConcurrency(env) : null;
        var label = chat.label();
        if (spec.isClaudeCli()) {
            var effort = spec.effort() != null
                    ? spec.effort()
                    : env.apply(spec.provider().extraEnv().get(LlmClientProperties.EXTRA_EFFORT));
            label = effort == null || effort.isBlank() ? label : label + "@" + effort.strip();
        }
        return new StepChat(chat, concurrency, spec.isClaude(), label);
    }

    public static int claudeCliConcurrency(UnaryOperator<String> env) {
        var raw = env.apply(CLAUDE_CLI_CONCURRENCY_ENV);
        if (raw == null || raw.isBlank()) {
            return DEFAULT_CLAUDE_CLI_CONCURRENCY;
        }
        try {
            return Math.max(1, Integer.parseInt(raw.strip()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid value for " + CLAUDE_CLI_CONCURRENCY_ENV + ": '" + raw + "'", e);
        }
    }

    private static String firstNonBlank(String... values) {
        for (var value : values) {
            if (value != null && !value.isBlank()) {
                return value.strip();
            }
        }
        return null;
    }
}
