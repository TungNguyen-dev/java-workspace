package tungnn.tutor.java.tool.finance.config;

import java.util.List;

public record AppleMailSourceConfig(
        String id,
        String mailRoot,
        List<String> mailboxes,
        List<String> senders,
        String accountId,
        String accountName) {

    public AppleMailSourceConfig {
        mailboxes = mailboxes == null ? List.of() : mailboxes;
        senders = senders == null ? List.of() : senders;
    }
}
