package tungnn.tutor.java.tool.finance;

import java.nio.file.Path;
import java.util.HashSet;
import tungnn.tutor.java.tool.finance.config.FinanceConfig;

public final class FinanceSyncApp {

    private FinanceSyncApp() {}

    public static void main(String[] args) {
        if (args.length == 0 || !"sync".equals(args[0])) {
            System.err.println("Usage: sync [--config path] [--source id]... [--dry-run]");
            System.exit(2);
        }
        var configPath = Path.of("storage/finance/finance-config.json");
        var sources = new HashSet<String>();
        var dryRun = false;
        for (var i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "--config" -> configPath = Path.of(args[++i]);
                case "--source" -> sources.add(args[++i]);
                case "--dry-run" -> dryRun = true;
                default -> {
                    System.err.println("Unknown argument: " + args[i]);
                    System.exit(2);
                }
            }
        }
        var service = FinanceSyncAssembler.assemble(FinanceConfig.load(configPath), System.getenv());
        var report = service.sync(sources, dryRun);
        System.out.println(report);
        if (report.failed() > 0) {
            System.exit(1);
        }
    }
}
