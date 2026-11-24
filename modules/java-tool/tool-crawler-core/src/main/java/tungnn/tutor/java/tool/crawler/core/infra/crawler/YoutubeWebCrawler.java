package tungnn.tutor.java.tool.crawler.core.infra.crawler;

import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import tungnn.tutor.java.selenium.element.ElementActions;
import tungnn.tutor.java.selenium.element.ElementFinder;
import tungnn.tutor.java.selenium.wait.Waits;
import tungnn.tutor.java.tool.crawler.core.domain.AbstractWebCrawler;
import tungnn.tutor.java.tool.crawler.core.domain.CrawlRequest;

public class YoutubeWebCrawler extends AbstractWebCrawler {

  private static final String TRANSCRIPT_GENERATOR_URL =
      "https://tactiq.io/tools/youtube-transcript";

  // Locators updated based on the new HTML structure
  private static final By TITLE_LOCATOR = By.cssSelector("#title h1 yt-formatted-string");
  private static final By FORM_LOCATOR = By.cssSelector("form[action*='youtube_transcript']");
  private static final By INPUT_LOCATOR = By.name("yt");
  private static final By SUBMIT_BTN_LOCATOR =
      By.cssSelector("input[type='submit'], button[type='submit']");
  private static final By TRANSCRIPT_CONTAINER_LOCATOR = By.id("transcript");
  private static final By TRANSCRIPT_ITEMS_LOCATOR = By.cssSelector("#transcript li");
  private static final By TRANSCRIPT_LINKS_LOCATOR = By.tagName("a");

  public YoutubeWebCrawler(WebDriver driver) {
    super(driver);
  }

  @Override
  public boolean supports(@NonNull CrawlRequest request) {
    return request.url().contains("youtube.com") || request.url().contains("youtu.be");
  }

  @Override
  protected String getTitle() {
    var element =
        Waits.waitUntil(
            driver, ExpectedConditions.visibilityOfElementLocated(TITLE_LOCATOR), timeout());
    return element.getText();
  }

  @Override
  protected String getContentAsHtml() {
    var currentUrl = driver.getCurrentUrl();
    requestTranscriptForVideo(currentUrl);
    return extractTranscriptHtml();
  }

  private void requestTranscriptForVideo(String videoUrl) {
    driver.navigate().to(TRANSCRIPT_GENERATOR_URL);

    var formElement =
        Waits.waitUntil(
            driver, ExpectedConditions.visibilityOfElementLocated(FORM_LOCATOR), timeout());

    var inputElement = ElementFinder.findElement(formElement, INPUT_LOCATOR);
    ElementActions.clear(inputElement);
    ElementActions.sendKeys(inputElement, videoUrl);

    var submitButton = ElementFinder.findElement(formElement, SUBMIT_BTN_LOCATOR);
    submitButton.submit();
  }

  private String extractTranscriptHtml() {
    Waits.waitUntil(
        driver,
        ExpectedConditions.visibilityOfAllElementsLocatedBy(TRANSCRIPT_ITEMS_LOCATOR),
        timeout());

    var transcriptContainer = ElementFinder.findElement(driver, TRANSCRIPT_CONTAINER_LOCATOR);

    return ElementFinder.findChildElements(transcriptContainer, TRANSCRIPT_LINKS_LOCATOR).stream()
        .map(link -> link.getAttribute("outerHTML"))
        .collect(Collectors.joining(" "));
  }
}
