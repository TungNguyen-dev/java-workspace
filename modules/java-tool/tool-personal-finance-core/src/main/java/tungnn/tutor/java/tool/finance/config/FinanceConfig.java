package tungnn.tutor.java.tool.finance.config;

import java.nio.file.Path;
import java.util.List;
import tungnn.tutor.java.jackson.JsonMapperUtils;
import tungnn.tutor.java.jackson.JsonUtils;

public record FinanceConfig(
        String storageDir,
        String sinkId,
        List<FileSourceConfig> fileSources,
        List<MailSourceConfig> mailSources,
        List<AppleMailSourceConfig> appleMailSources,
        List<StatementLayout> layouts,
        List<CategoryRule> categoryRules,
        List<String> categories) {

    public FinanceConfig {
        fileSources = fileSources == null ? List.of() : fileSources;
        mailSources = mailSources == null ? List.of() : mailSources;
        appleMailSources = appleMailSources == null ? List.of() : appleMailSources;
        layouts = layouts == null ? List.of() : layouts;
        categoryRules = categoryRules == null ? List.of() : categoryRules;
        categories = categories == null ? List.of() : categories;
    }

    public static FinanceConfig load(Path path) {
        return JsonUtils.readFromFile(JsonMapperUtils.createJsonMapper(), path, FinanceConfig.class);
    }
}
