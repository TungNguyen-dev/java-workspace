package tungnn.tutor.java.starter.infrastructure.webpage;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import tungnn.tutor.java.selenium.util.ElementUtil;
import tungnn.tutor.java.starter.infrastructure.webpage.crawler.PageCrawlResult;
import tungnn.tutor.java.starter.infrastructure.webpage.crawler.PageCrawler;

public class JavaJLSPage extends BasePage implements PageCrawler {

  public JavaJLSPage(WebDriver driver) {
    super(driver);
  }

  @Override
  protected Duration timeout() {
    return Duration.ofSeconds(30);
  }

  @Override
  protected String homeUrl() {
    return "https://docs.oracle.com/javase/specs/jls/se26/html/index.html";
  }

  @Override
  public PageCrawlResult crawl(String url) {
    navigateTo(url);

    var id = "";
    if (url.contains("#")) {
      id = url.split("#")[1];
    } else {
      id = url.substring(url.lastIndexOf('/') + 1).replace(".html", "");
    }

    var root = ElementUtil.findElement(driver, By.id(id), timeout());

    return new PageCrawlResult(url, getTitle(root), getContent(root, id));
  }

  private String getTitle(WebElement root) {
    return ElementUtil.findChildElement(root, By.className("titlepage")).getText();
  }

  private String getContent(WebElement root, String id) {
    var paragraphs = ElementUtil.findElements(root, By.cssSelector("p[id^='" + id + "']"));

    var content = new StringBuilder();
    for (var paragraph : paragraphs) {
      content.append(paragraph.getText()).append("\n\n");
    }

    return content.toString();
  }
}
