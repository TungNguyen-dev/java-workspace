package tungnn.tutor.java.tool.crawler;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import tungnn.tutor.java.core.lib.io.filesystem.FileNameUtils;
import tungnn.tutor.java.core.lib.io.filesystem.FileUtils;
import tungnn.tutor.java.selenium.pool.GenericWebDriverPool;
import tungnn.tutor.java.selenium.pool.WebDriverPoolConfig;
import tungnn.tutor.java.tool.crawler.core.WebCrawler;
import tungnn.tutor.java.tool.crawler.core.WebCrawlerRegistry;
import tungnn.tutor.java.tool.crawler.service.DefaultWebCrawlService;

public class WebCrawlApp {

    private static final String USAGE = "Usage: WebCrawlApp <url>... [--out dir] [--drivers n]";
    private static final Path DEFAULT_OUT = Path.of("storage", "tool-crawler-core");
    private static final int DEFAULT_DRIVERS = 2;

    record Args(List<String> urls, Path out, int drivers) {}

    static void main(String[] args) {
        Args parsed;
        try {
            parsed = parse(args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.err.println(USAGE);
            System.exit(1);
            return;
        }

        var failed = 0;
        try (var driverPool =
                        new GenericWebDriverPool(WebDriverPoolConfig.fromEnv().withMaxTotal(parsed.drivers()));
                var service = new DefaultWebCrawlService(
                        driverPool, WebCrawlerRegistry.defaults(), DefaultWebCrawlService.Options.defaults())) {
            var requests =
                    parsed.urls().stream().map(WebCrawler.Request.PageUrl::new).toList();
            FileUtils.createDirectories(parsed.out());
            for (var result : service.crawlAll(requests)) {
                switch (result) {
                    case WebCrawler.Result.Success success -> save(parsed.out(), success);
                    case WebCrawler.Result.Failure(var url, var exception) -> {
                        failed++;
                        System.err.println("Failed " + url + ": " + exception.getMessage());
                    }
                }
            }
        }
        System.out.println("Done: " + (parsed.urls().size() - failed) + " succeeded, " + failed + " failed");
        if (failed > 0) {
            System.exit(1);
        }
    }

    static Args parse(String[] args) {
        var urls = new ArrayList<String>();
        var out = DEFAULT_OUT;
        var drivers = DEFAULT_DRIVERS;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--out" -> out = Path.of(value(args, ++i, "--out"));
                case "--drivers" -> drivers = parseDrivers(value(args, ++i, "--drivers"));
                default -> {
                    if (args[i].startsWith("--")) {
                        throw new IllegalArgumentException("Unknown option: " + args[i]);
                    }
                    urls.add(args[i]);
                }
            }
        }
        if (urls.isEmpty()) {
            throw new IllegalArgumentException("Missing url");
        }
        return new Args(List.copyOf(urls), out, drivers);
    }

    private static String value(String[] args, int index, String option) {
        if (index >= args.length) {
            throw new IllegalArgumentException("Missing value for " + option);
        }
        return args[index];
    }

    private static int parseDrivers(String text) {
        try {
            var drivers = Integer.parseInt(text);
            if (drivers > 0) {
                return drivers;
            }
        } catch (NumberFormatException ignored) {
        }
        throw new IllegalArgumentException("--drivers must be a positive number: " + text);
    }

    private static void save(Path out, WebCrawler.Result.Success success) {
        var file = out.resolve(FileNameUtils.sanitize(success.title()) + ".md");
        FileUtils.writeString(
                file, "# %s%n%nSource: %s%n%n%s".formatted(success.title(), success.url(), success.content()));
        System.out.println("Saved " + file);
    }
}
