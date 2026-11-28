package tungnn.tutor.java.tool.finance.core.dedupe;

import tungnn.tutor.java.tool.finance.core.model.SourceKind;

public final class SourcePriority {

    private SourcePriority() {
        throw new UnsupportedOperationException("cannot instantiate utility class");
    }

    public static int rank(SourceKind kind) {
        return switch (kind) {
            case BANK_WEB -> 3;
            case BANK_STATEMENT_FILE, CREDIT_CARD_FILE, E_WALLET_FILE -> 2;
            case EMAIL -> 1;
        };
    }
}
