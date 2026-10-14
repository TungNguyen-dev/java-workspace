package tungnn.tutor.java.tool.crawler.core.infrastructure.crawler;

import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import tungnn.tutor.java.selenium.util.ElementUtil;
import tungnn.tutor.java.tool.crawler.core.domain.AbstractWebCrawler;
import tungnn.tutor.java.tool.crawler.core.domain.CrawlRequest;

public class YoutubeWebCrawler extends AbstractWebCrawler {

  private static final String TRANSCRIPT_GENERATOR_URL =
      "https://tactiq.io/tools/youtube-transcript";

  // Locators defined as constants for better readability and maintenance
  private static final By TITLE_LOCATOR = By.cssSelector("#title h1 yt-formatted-string");
  private static final By FORM_LOCATOR = By.id("wf-form-YouTube-Transcript");
  private static final By INPUT_LOCATOR =
      By.cssSelector("input[type='text'], input[type='url'], input:not([type='submit'])");
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
        ElementUtil.waitUntil(
            driver, ExpectedConditions.visibilityOfElementLocated(TITLE_LOCATOR), timeout());
    return element.getText();
  }

  @Override
  protected String getContentAsHtml() {
    var currentUrl = driver.getCurrentUrl();
    requestTranscriptForVideo(currentUrl);
    return extractTranscriptHtml();
  }

  /** Navigates to the transcript tool and submits the YouTube video URL to generate transcript. */
  private void requestTranscriptForVideo(String videoUrl) {
    driver.navigate().to(TRANSCRIPT_GENERATOR_URL);

    var formElement =
        ElementUtil.waitUntil(
            driver, ExpectedConditions.visibilityOfElementLocated(FORM_LOCATOR), timeout());

    var inputElement = ElementUtil.findElement(formElement, INPUT_LOCATOR);
    ElementUtil.clear(inputElement);
    ElementUtil.sendKeys(inputElement, videoUrl);

    var submitButton = ElementUtil.findElement(formElement, SUBMIT_BTN_LOCATOR);
    submitButton.submit();
  }

  /** Waits for the transcript elements to render and extracts their HTML links. */
  private String extractTranscriptHtml() {
    ElementUtil.waitUntil(
        driver,
        ExpectedConditions.visibilityOfAllElementsLocatedBy(TRANSCRIPT_ITEMS_LOCATOR),
        timeout());

    var transcriptContainer = ElementUtil.findElement(driver, TRANSCRIPT_CONTAINER_LOCATOR);

    return ElementUtil.findChildElements(transcriptContainer, TRANSCRIPT_LINKS_LOCATOR).stream()
        .map(link -> link.getAttribute("outerHTML"))
        .collect(Collectors.joining(" "));
  }
}
