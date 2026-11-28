package tungnn.tutor.java.tool.finance.core.categorize;

import java.util.List;
import java.util.Optional;
import tungnn.tutor.java.ai.client.LlmClient;
import tungnn.tutor.java.tool.finance.core.model.Transaction;
import tungnn.tutor.java.tool.finance.utils.PromptUtils;

public final class LlmCategorizer implements Categorizer {

    private final LlmClient client;
    private final List<String> categories;
    private final String systemPrompt;

    public LlmCategorizer(LlmClient client, List<String> categories) {
        this.client = client;
        this.categories = List.copyOf(categories);
        this.systemPrompt = PromptUtils.load("categorize.md").replace("{{categories}}", String.join("\n", categories));
    }

    @Override
    public Optional<String> categorize(Transaction transaction) {
        if (categories.isEmpty() || transaction.description() == null) {
            return Optional.empty();
        }
        try {
            var answer = client.chat(systemPrompt, transaction.description());
            return categories.stream()
                    .filter(category -> category.equalsIgnoreCase(answer == null ? "" : answer.strip()))
                    .findFirst();
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }
}
