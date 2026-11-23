package tungnn.tutor.java.tool.doctrans.core.infra.page;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import tungnn.tutor.java.core.lib.multithread.ThreadUtils;
import tungnn.tutor.java.selenium.util.ElementUtil;

public class GeminiPage {

  private static final String HOME_PAGE = "https://gemini.google.com/app";
  private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);
  private static final Duration GENERATION_TIMEOUT = Duration.ofMinutes(5);

  private final WebDriver webDriver;

  public GeminiPage(WebDriver webDriver) {
    this.webDriver = webDriver;
  }

  public String chat(String prompt, List<Path> filePaths) {
    navigateToHomePage();
    uploadFiles(filePaths);
    enterPrompt(prompt);
    clickSendButton();

    long minMillis = 1_000L;
    long maxMillis = 15_000L;
    long randomMillis = ThreadLocalRandom.current().nextLong(minMillis, maxMillis + 1);
    ThreadUtils.sleep(randomMillis);

    WebElement responseBlock = waitForAndGetResponse();
    return ElementUtil.getText(responseBlock);
  }

  private void navigateToHomePage() {
    webDriver.get(HOME_PAGE);
  }

  private void uploadFiles(List<Path> filePaths) {
    if (filePaths == null || filePaths.isEmpty()) {
      return;
    }
    // TODO: Unsupported feature currently. Will be fully implemented in the future.
    throw new UnsupportedOperationException("uploadFiles is not yet implemented");
  }

  private void enterPrompt(String prompt) {
    // Gemini uses a contenteditable div for the chat input field
    By inputLocator =
        By.xpath(
            "//div[@contenteditable='true' and (contains(@aria-label, 'prompt') or contains(@class, 'ql-editor'))]");
    WebElement inputArea =
        ElementUtil.findClickableElement(webDriver, inputLocator, DEFAULT_TIMEOUT);

    String htmlPrompt = "<p>%s</p>".formatted(prompt);
    inputArea.click();

    // JavaScript snippet handling Trusted Types to bypass the security restriction
    String script =
        """
        arguments[0].focus();
        let html = arguments[1];
        if (window.trustedTypes && window.trustedTypes.createPolicy) {
          try {
            const policy = window.trustedTypes.createPolicy('seleniumIdentityPolicy_' + Math.random(), {
              createHTML: (string) => string
            });
            html = policy.createHTML(html);
          } catch (e) {
            console.warn('TrustedTypes policy creation failed:', e);
          }
        }
        arguments[0].innerHTML = html;
        arguments[0].dispatchEvent(new Event('input', { bubbles: true }));
        """;

    ((JavascriptExecutor) webDriver).executeScript(script, inputArea, htmlPrompt);
  }

  private void clickSendButton() {
    By sendButtonLocator = By.xpath("//gem-icon-button[contains(@class, 'send-button')]//button");
    WebDriverWait wait = new WebDriverWait(webDriver, DEFAULT_TIMEOUT);
    WebElement sendButton = wait.until(ExpectedConditions.elementToBeClickable(sendButtonLocator));
    sendButton.click();
  }

  private WebElement waitForAndGetResponse() {
    By responseBlockLocator =
        By.xpath(
            "(//div[contains(@class, 'markdown') and contains(@id, 'model-response-message-content')])[last()]");
    WebDriverWait wait = new WebDriverWait(webDriver, GENERATION_TIMEOUT);
    return wait.until(driver -> {
      try {
        WebElement el = driver.findElement(responseBlockLocator);
        String ariaBusy = el.getAttribute("aria-busy");
        if ("false".equals(ariaBusy)) {
          String text = el.getText();
          if (text != null && !text.trim().isEmpty()) {
            return el;
          }
        }
      } catch (Exception ignored) {
        // Ignore transient exceptions while the element is being dynamically rendered
      }
      return null;
    });
  }
}
