package tungnn.tutor.java.tool.crawler.core.infra.crawler;

import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import tungnn.tutor.java.selenium.element.ElementFinder;
import tungnn.tutor.java.selenium.element.ElementInfo;
import tungnn.tutor.java.tool.crawler.core.domain.AbstractWebCrawler;
import tungnn.tutor.java.tool.crawler.core.domain.CrawlRequest;

public class JavaJlsWebCrawler extends AbstractWebCrawler {

    public JavaJlsWebCrawler(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean supports(CrawlRequest request) {
        return request.url().contains("docs.oracle.com/javase/specs");
    }

    @Override
    protected String getTitle() {
        return ElementFinder.findChildElement(root(), By.className("titlepage")).getText();
    }

    @Override
    protected String getContentAsHtml() {
        // The id comes from the page URL, so quotes are stripped to keep the XPath literal valid.
        var id = sectionId().replace("'", "");
        var paragraphs = ElementFinder.findElements(root(), By.xpath("descendant::p[starts-with(@id, '" + id + "')]"));
        return paragraphs.stream()
                .map(paragraph -> ElementInfo.getAttribute(paragraph, "outerHTML"))
                .collect(Collectors.joining("\n"));
    }

    private WebElement root() {
        return ElementFinder.findElement(driver, By.id(sectionId()), timeout());
    }

    private String sectionId() {
        var url = driver.getCurrentUrl();
        var fragmentIndex = url.indexOf('#');
        if (fragmentIndex >= 0 && fragmentIndex < url.length() - 1) {
            return url.substring(fragmentIndex + 1);
        }
        return url.substring(url.lastIndexOf('/') + 1).replace(".html", "");
    }
}
