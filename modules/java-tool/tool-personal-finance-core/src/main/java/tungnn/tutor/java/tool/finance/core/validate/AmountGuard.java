package tungnn.tutor.java.tool.finance.core.validate;

import java.math.BigDecimal;
import java.util.Set;
import tungnn.tutor.java.tool.finance.utils.AmountUtils;

public final class AmountGuard {

    private final Set<String> numbersInSource;

    public AmountGuard(String sourceText) {
        this.numbersInSource = AmountUtils.numbersIn(sourceText);
    }

    public boolean isPresent(BigDecimal amount) {
        return amount != null && numbersInSource.contains(AmountUtils.canonical(amount));
    }
}
