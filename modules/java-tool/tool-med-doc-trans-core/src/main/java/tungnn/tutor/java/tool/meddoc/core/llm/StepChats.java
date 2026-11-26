package tungnn.tutor.java.tool.meddoc.core.llm;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tungnn.tutor.java.tool.meddoc.core.service.Step;

public final class StepChats {

    private static final Logger LOGGER = LoggerFactory.getLogger(StepChats.class);

    private final Function<Step, StepChat> factory;
    private final Predicate<Step> claudeLookup;
    private final Map<Step, StepChat> created = new ConcurrentHashMap<>();

    private StepChats(Function<Step, StepChat> factory, Predicate<Step> claudeLookup) {
        this.factory = Objects.requireNonNull(factory, "factory must not be null");
        this.claudeLookup = claudeLookup;
    }

    public static StepChats of(Function<Step, StepChat> factory) {
        return new StepChats(factory, null);
    }

    public static StepChats of(Map<Step, ChatSpec> specs, UnaryOperator<String> env) {
        Objects.requireNonNull(specs, "specs must not be null");
        return new StepChats(
                step -> {
                    var spec = specs.get(step);
                    if (spec == null) {
                        throw new IllegalArgumentException("No LLM configured for step " + step.cliName());
                    }
                    LOGGER.info("[{}] using LLM {}", step.cliName(), spec);
                    return ChatSpecUtils.create(spec, env);
                },
                step -> specs.get(step) != null && specs.get(step).isClaude());
    }

    public boolean usesClaude(Step step) {
        Objects.requireNonNull(step, "step must not be null");
        if (claudeLookup != null) {
            return claudeLookup.test(step);
        }
        var chat = forStep(step);
        return chat != null && chat.claude();
    }

    public String usageLine(Step step) {
        var chat = created.get(step);
        if (chat != null
                && chat.chat() instanceof ClaudeCliMarkdownChat cli
                && cli.usage().calls() > 0) {
            return cli.usageLine(step.cliName());
        }
        return null;
    }

    public List<String> usageLines() {
        return Arrays.stream(Step.values())
                .map(this::usageLine)
                .filter(Objects::nonNull)
                .toList();
    }

    public StepChat forStep(Step step) {
        Objects.requireNonNull(step, "step must not be null");
        return created.computeIfAbsent(step, factory);
    }
}
