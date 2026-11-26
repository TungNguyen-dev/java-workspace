package tungnn.tutor.java.tool.crawler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class WebCrawlAppTest {

    @Test
    void parseUsesDefaults() {
        var args = WebCrawlApp.parse(new String[] {"https://a.com"});

        assertThat(args.urls()).containsExactly("https://a.com");
        assertThat(args.out()).isEqualTo(Path.of("storage", "tool-crawler-core"));
        assertThat(args.drivers()).isEqualTo(2);
    }

    @Test
    void parseReadsOptionsAndMultipleUrls() {
        var args = WebCrawlApp.parse(new String[] {"https://a.com", "--out", "out", "https://b.com", "--drivers", "4"});

        assertThat(args.urls()).containsExactly("https://a.com", "https://b.com");
        assertThat(args.out()).isEqualTo(Path.of("out"));
        assertThat(args.drivers()).isEqualTo(4);
    }

    @Test
    void parseRejectsMissingUrl() {
        assertThatThrownBy(() -> WebCrawlApp.parse(new String[] {"--out", "x"}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Missing url");
    }

    @Test
    void parseRejectsUnknownOption() {
        assertThatThrownBy(() -> WebCrawlApp.parse(new String[] {"https://a.com", "--nope"}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown option: --nope");
    }

    @Test
    void parseRejectsMissingOptionValue() {
        assertThatThrownBy(() -> WebCrawlApp.parse(new String[] {"https://a.com", "--out"}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Missing value for --out");
    }

    @Test
    void parseRejectsNonPositiveDrivers() {
        assertThatThrownBy(() -> WebCrawlApp.parse(new String[] {"https://a.com", "--drivers", "0"}))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> WebCrawlApp.parse(new String[] {"https://a.com", "--drivers", "x"}))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
