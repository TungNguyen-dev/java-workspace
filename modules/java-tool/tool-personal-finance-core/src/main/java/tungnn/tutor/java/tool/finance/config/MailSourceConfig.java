package tungnn.tutor.java.tool.finance.config;

import java.util.List;

public record MailSourceConfig(
        String id, String folder, List<String> senders, String accountId, String accountName) {}
