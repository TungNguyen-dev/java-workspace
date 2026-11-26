package tungnn.tutor.java.tool.crawler.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import org.openqa.selenium.WebDriver;
import tungnn.tutor.java.tool.crawler.core.impl.AwsDocSdkWebCrawler;
import tungnn.tutor.java.tool.crawler.core.impl.CourseraWebCrawler;
import tungnn.tutor.java.tool.crawler.core.impl.GenericWebCrawler;
import tungnn.tutor.java.tool.crawler.core.impl.JavaJlsWebCrawler;
import tungnn.tutor.java.tool.crawler.core.impl.SpringDocWebCrawler;
import tungnn.tutor.java.tool.crawler.core.impl.YoutubeWebCrawler;

public class WebCrawlerRegistry {

    private static final int UNBOUNDED = Integer.MAX_VALUE;

    private final List<Spec> specs;
    private final Spec fallback;

    public WebCrawlerRegistry(List<Spec> specs, Spec fallback) {
        this.specs = List.copyOf(Objects.requireNonNull(specs, "specs must not be null"));
        this.fallback = Objects.requireNonNull(fallback, "fallback must not be null");
    }

    public static WebCrawlerRegistry defaults() {
        var specs = List.of(
                new Spec("coursera", urlContains("coursera.org"), CourseraWebCrawler::new, 1),
                new Spec(
                        "youtube", urlContains("youtube.com").or(urlContains("youtu.be")), YoutubeWebCrawler::new, 4),
                new Spec(
                        "java-jls", urlContains("docs.oracle.com/javase/specs"), JavaJlsWebCrawler::new, UNBOUNDED),
                new Spec("spring-doc", urlContains("docs.spring.io"), SpringDocWebCrawler::new, UNBOUNDED),
                new Spec("aws-doc", urlContains("docs.aws.amazon.com"), AwsDocSdkWebCrawler::new, UNBOUNDED),
                new Spec("baeldung", urlContains("baeldung.com"), GenericWebCrawler::new, 1),
                new Spec(
                        "anthropic-partner",
                        urlContains("anthropic-partners.skilljar.com"),
                        GenericWebCrawler::new,
                        1));
        var fallback = new Spec("generic", _ -> true, GenericWebCrawler::new, 3);
        return new WebCrawlerRegistry(specs, fallback);
    }

    public Spec resolve(WebCrawler.Request request) {
        return specs.stream()
                .filter(spec -> spec.matcher().test(request))
                .findFirst()
                .orElse(fallback);
    }

    public List<Spec> all() {
        var all = new ArrayList<>(specs);
        all.add(fallback);
        return List.copyOf(all);
    }

    private static Predicate<WebCrawler.Request> urlContains(String fragment) {
        return request ->
                request.url() != null && request.url().toLowerCase(Locale.ROOT).contains(fragment);
    }

    public record Spec(
            String name,
            Predicate<WebCrawler.Request> matcher,
            Function<WebDriver, WebCrawler> factory,
            int maxConcurrency) {

        public Spec {
            Objects.requireNonNull(name, "name must not be null");
            Objects.requireNonNull(matcher, "matcher must not be null");
            Objects.requireNonNull(factory, "factory must not be null");
            if (maxConcurrency <= 0) {
                throw new IllegalArgumentException("maxConcurrency must be positive: " + maxConcurrency);
            }
        }
    }
}
