package tungnn.tutor.java.starter.infrastructure.webpage;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import tungnn.tutor.java.selenium.util.ElementUtil;
import tungnn.tutor.java.starter.infrastructure.webpage.crawler.PageCrawlResult;
import tungnn.tutor.java.starter.infrastructure.webpage.crawler.PageCrawler;

public class AwsDocSdkPage extends BasePage implements PageCrawler {

  public AwsDocSdkPage(WebDriver driver) {
    super(driver);
  }

  @Override
  protected Duration timeout() {
    return Duration.ofSeconds(30);
  }

  @Override
  protected String homeUrl() {
    return "https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html";
  }

  @Override
  public PageCrawlResult crawl(String url) {
    navigateTo(url);
    return new PageCrawlResult(url, getTitle(), getContent());
  }

  private String getTitle() {
    var locator = By.id("skip-link");
    return ElementUtil.findElement(driver, locator).getText();
  }

  private String getContent() {
    var locator = By.id("main-col-body");
    return ElementUtil.findElement(driver, locator).getText();
  }
}
