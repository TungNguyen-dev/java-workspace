package tungnn.tutor.java.tool.crawler.core.infra.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import tungnn.tutor.java.tool.crawler.core.domain.WebCrawlRequest;
import tungnn.tutor.java.tool.crawler.core.infra.crawler.AwsDocSdkWebCrawler;
import tungnn.tutor.java.tool.crawler.core.infra.crawler.CourseraWebCrawler;
import tungnn.tutor.java.tool.crawler.core.infra.crawler.GenericWebCrawler;
import tungnn.tutor.java.tool.crawler.core.infra.crawler.JavaJlsWebCrawler;
import tungnn.tutor.java.tool.crawler.core.infra.crawler.SpringDocWebCrawler;
import tungnn.tutor.java.tool.crawler.core.infra.crawler.YoutubeWebCrawler;

public class WebCrawlerRegistry {

    private static final int UNBOUNDED = Integer.MAX_VALUE;

    private final List<WebCrawlerSpec> specs;
    private final WebCrawlerSpec fallback;

    public WebCrawlerRegistry(List<WebCrawlerSpec> specs, WebCrawlerSpec fallback) {
        this.specs = List.copyOf(Objects.requireNonNull(specs, "specs must not be null"));
        this.fallback = Objects.requireNonNull(fallback, "fallback must not be null");
    }

    public static WebCrawlerRegistry defaults() {
        var specs = List.of(
                new WebCrawlerSpec("coursera", urlContains("coursera.org"), CourseraWebCrawler::new, 1),
                new WebCrawlerSpec(
                        "youtube",
                        urlContains("youtube.com").or(urlContains("youtu.be")),
                        YoutubeWebCrawler::new,
                        4),
                new WebCrawlerSpec(
                        "java-jls", urlContains("docs.oracle.com/javase/specs"), JavaJlsWebCrawler::new, UNBOUNDED),
                new WebCrawlerSpec("spring-doc", urlContains("docs.spring.io"), SpringDocWebCrawler::new, UNBOUNDED),
                new WebCrawlerSpec(
                        "aws-doc", urlContains("docs.aws.amazon.com"), AwsDocSdkWebCrawler::new, UNBOUNDED),
                new WebCrawlerSpec("baeldung", urlContains("baeldung.com"), GenericWebCrawler::new, 1),
                new WebCrawlerSpec(
                        "anthropic-partner",
                        urlContains("anthropic-partners.skilljar.com"),
                        GenericWebCrawler::new,
                        1));
        var fallback = new WebCrawlerSpec("generic", request -> true, GenericWebCrawler::new, 3);
        return new WebCrawlerRegistry(specs, fallback);
    }

    public WebCrawlerSpec resolve(WebCrawlRequest request) {
        return specs.stream().filter(spec -> spec.matcher().test(request)).findFirst().orElse(fallback);
    }

    public List<WebCrawlerSpec> all() {
        var all = new ArrayList<>(specs);
        all.add(fallback);
        return List.copyOf(all);
    }

    private static Predicate<WebCrawlRequest> urlContains(String fragment) {
        return request -> request.url().contains(fragment);
    }
}
