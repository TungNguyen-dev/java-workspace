package tungnn.tutor.java.tool.finance.core.categorize;

import java.util.List;
import java.util.Optional;
import tungnn.tutor.java.tool.finance.core.model.Transaction;

public final class ChainCategorizer implements Categorizer {

    private final List<Categorizer> chain;

    public ChainCategorizer(List<Categorizer> chain) {
        this.chain = List.copyOf(chain);
    }

    @Override
    public Optional<String> categorize(Transaction transaction) {
        for (var categorizer : chain) {
            var result = categorizer.categorize(transaction);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }
}
