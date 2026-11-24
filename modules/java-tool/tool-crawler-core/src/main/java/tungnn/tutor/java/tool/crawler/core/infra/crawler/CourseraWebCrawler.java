package tungnn.tutor.java.tool.crawler.core.infra.crawler;

import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import tungnn.tutor.java.selenium.element.ElementInfo;
import tungnn.tutor.java.selenium.wait.Waits;
import tungnn.tutor.java.tool.crawler.core.domain.AbstractWebCrawler;

public class CourseraWebCrawler extends AbstractWebCrawler {

    // Locators defined as constants for easier maintenance
    private static final By VIDEO_TITLE_LOCATOR = By.cssSelector("h1.video-name");
    private static final By TRANSCRIPT_TAB_BTN_LOCATOR =
            By.cssSelector("[data-testid='item-tool-panel-button-transcript']");
    private static final By TRANSCRIPT_CONTAINERS_LOCATOR = By.cssSelector("div.phrases");

    public CourseraWebCrawler(WebDriver driver) {
        super(driver);
    }

    @Override
    protected String getTitle() {
        var element =
                Waits.waitUntil(driver, ExpectedConditions.visibilityOfElementLocated(VIDEO_TITLE_LOCATOR), timeout());
        return element.getText();
    }

    @Override
    protected String getContentAsHtml() {
        ensureTranscriptTabIsOpen();

        // Wait explicitly until at least one transcript container becomes visible
        Waits.waitUntil(
                driver, ExpectedConditions.visibilityOfElementLocated(TRANSCRIPT_CONTAINERS_LOCATOR), timeout());

        // Fetch all elements matching the transcript containers locator
        var transcriptContainers = driver.findElements(TRANSCRIPT_CONTAINERS_LOCATOR);

        // Concatenate the innerHTML of all found elements
        return transcriptContainers.stream()
                .map(element -> ElementInfo.getAttribute(element, "innerHTML"))
                .collect(Collectors.joining("\n"));
    }

    private void ensureTranscriptTabIsOpen() {
        var transcriptButton =
                Waits.waitUntil(driver, ExpectedConditions.elementToBeClickable(TRANSCRIPT_TAB_BTN_LOCATOR), timeout());

        var isPressed = Boolean.parseBoolean(ElementInfo.getAttribute(transcriptButton, "aria-pressed"));
        if (!isPressed) {
            transcriptButton.click();

            // Wait explicitly until the transcript containers become visible.
            Waits.waitUntil(
                    driver, ExpectedConditions.visibilityOfElementLocated(TRANSCRIPT_CONTAINERS_LOCATOR), timeout());
        }
    }
}
