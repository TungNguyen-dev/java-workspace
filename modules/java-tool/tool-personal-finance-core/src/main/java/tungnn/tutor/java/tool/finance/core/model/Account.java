package tungnn.tutor.java.tool.finance.core.model;

import java.util.Objects;

public record Account(String id, String name, SourceKind kind, String currency) {

    public Account {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(kind, "kind must not be null");
        Objects.requireNonNull(currency, "currency must not be null");
    }
}
