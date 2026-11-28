package tungnn.tutor.java.tool.meddoc.llm;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import tungnn.tutor.java.ai.config.LlmClientProperties;
import tungnn.tutor.java.ai.util.LlmClientUtils;
import tungnn.tutor.java.claude.cli.ClaudeCliOptions;
import tungnn.tutor.java.tool.meddoc.core.Step;

public final class ChatSpecUtils {

    public static final String DEFAULT_KEY = "default";
    public static final String ENV_DEFAULT = "DOCTRANS_LLM_DEFAULT";
    public static final String ENV_STEP_PREFIX = "DOCTRANS_LLM_";
    public static final String ENV_LEGACY_PROVIDER = "DOCTRANS_TRANSLATOR_PROVIDER";
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
        ChatSpec.parse(spec);
    }

    public static Map<Step, ChatSpec> resolve(Map<String, String> overrides, UnaryOperator<String> env) {
        overrides.forEach(ChatSpecUtils::checkOverride);
        var defaultSpec = ChatSpec.parse(firstNonBlank(
                overrides.get(DEFAULT_KEY), env.apply(ENV_DEFAULT), env.apply(ENV_LEGACY_PROVIDER), FALLBACK));
        var specs = new EnumMap<Step, ChatSpec>(Step.class);
        for (var step : Step.values()) {
            if (step.usesLlm()) {
                var configured =
                        firstNonBlank(overrides.get(step.cliName()), env.apply(ENV_STEP_PREFIX + step.envName()), null);
                specs.put(step, configured == null ? defaultSpec : ChatSpec.parse(configured));
            }
        }
        return specs;
    }

    public static StepChat create(ChatSpec spec, UnaryOperator<String> env) {
        if (spec.isClaudeCli()) {
            var options = ClaudeCliOptions.fromEnv(env).withModel(spec.model()).withEffort(spec.effort());
            var chat = new ClaudeCliMarkdownChat(options);
            return new StepChat(chat, ClaudeCliMarkdownChat.concurrency(env), true, chat.label());
        }
        var provider = spec.provider();
        UnaryOperator<String> withModel = key ->
                spec.model() != null && provider.modelEnv().filter(key::equals).isPresent()
                        ? spec.model()
                        : env.apply(key);
        var client = LlmClientUtils.create(LlmClientProperties.fromEnv(provider, withModel));
        return LlmClientMarkdownChat.stepChat(client, spec.isClaude());
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
