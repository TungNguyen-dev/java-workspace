package tungnn.tutor.java.tool.finance.core.categorize;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import tungnn.tutor.java.tool.finance.config.CategoryRule;
import tungnn.tutor.java.tool.finance.core.model.Transaction;

public final class RuleCategorizer implements Categorizer {

    private final List<CategoryRule> rules;

    public RuleCategorizer(List<CategoryRule> rules) {
        this.rules = List.copyOf(rules);
    }

    @Override
    public Optional<String> categorize(Transaction transaction) {
        if (transaction.description() == null) {
            return Optional.empty();
        }
        var description = transaction.description().toLowerCase(Locale.ROOT);
        return rules.stream()
                .filter(rule -> description.contains(rule.keyword().toLowerCase(Locale.ROOT)))
                .map(CategoryRule::category)
                .findFirst();
    }
}
