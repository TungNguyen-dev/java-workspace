package tungnn.tutor.java.tool.finance.config;

import tungnn.tutor.java.tool.finance.core.model.SourceKind;

public record FileSourceConfig(
        String id, SourceKind kind, String directory, String accountId, String accountName, String layoutId) {}
