package tungnn.tutor.java.tool.finance;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashSet;
import tungnn.tutor.java.tool.finance.config.FinanceConfig;

public final class FinanceSyncApp {

    private static final String USAGE =
            "Usage: sync [--config path] [--source id]... [--dry-run] | note [--config path] [--date yyyy-MM-dd]";

    private FinanceSyncApp() {}

    public static void main(String[] args) {
        if (args.length == 0 || !("sync".equals(args[0]) || "note".equals(args[0]))) {
            System.err.println(USAGE);
            System.exit(2);
        }
        var command = args[0];
        var configPath = Path.of("storage/finance/finance-config.json");
        var sources = new HashSet<String>();
        var dryRun = false;
        var date = LocalDate.now();
        for (var i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "--config" -> configPath = Path.of(args[++i]);
                case "--source" -> sources.add(args[++i]);
                case "--dry-run" -> dryRun = true;
                case "--date" -> date = LocalDate.parse(args[++i]);
                default -> {
                    System.err.println("Unknown argument: " + args[i]);
                    System.exit(2);
                }
            }
        }
        var app = FinanceSyncAssembler.assemble(FinanceConfig.load(configPath), System.getenv());
        if ("note".equals(command)) {
            System.out.println("Daily note: " + app.notes().write(date));
            return;
        }
        var report = app.sync().sync(sources, dryRun);
        System.out.println(report);
        if (!dryRun) {
            report.writtenDates()
                    .forEach(day ->
                            System.out.println("Daily note: " + app.notes().write(day)));
        }
        if (report.failed() > 0) {
            System.exit(1);
        }
    }
}
