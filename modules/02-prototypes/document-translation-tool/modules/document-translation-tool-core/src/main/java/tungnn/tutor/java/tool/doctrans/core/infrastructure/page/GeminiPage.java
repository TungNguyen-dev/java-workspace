package tungnn.tutor.java.tool.doctrans.core.infrastructure.page;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.openqa.selenium.By;
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
    if (filePaths.isEmpty()) {
      return;
    }

    // TODO: Unsupported feature currently. Will be fully implemented in the future.
    throw new UnsupportedOperationException("uploadFiles is not yet implemented");

    //    if (filePaths != null && !filePaths.isEmpty()) {
    //      By fileInputLocator = By.xpath("//input[@type='file']");
    //      WebElement fileInput = ElementUtil.findElement(webDriver, fileInputLocator,
    // DEFAULT_TIMEOUT);
    //      ElementUtil.uploadFiles(fileInput, filePaths);
    //      ThreadUtils.sleep(5000);
    //    }
  }

  private void enterPrompt(String prompt) {
    // Gemini uses a contenteditable div for the chat input field
    By inputLocator =
        By.xpath(
            "//div[@contenteditable='true' and (contains(@aria-label, 'prompt') or contains(@class, 'ql-editor'))]");
    WebElement inputArea =
        ElementUtil.findClickableElement(webDriver, inputLocator, DEFAULT_TIMEOUT);

    var htmlPrompt = "<p>%s</p>".formatted(prompt);

    // Focus on the input area
    inputArea.click();

    // JavaScript snippet handling Trusted Types to bypass the security restriction
    String script =
        "arguments[0].focus();\n"
            + "let html = arguments[1];\n"
            + "if (window.trustedTypes && window.trustedTypes.createPolicy) {\n"
            + "    try {\n"
            + "        // Create a custom Trusted Types policy to allow raw HTML injection\n"
            + "        const policy = window.trustedTypes.createPolicy('seleniumIdentityPolicy_' + Math.random(), {\n"
            + "            createHTML: (string) => string\n"
            + "        });\n"
            + "        html = policy.createHTML(html);\n"
            + "    } catch (e) {\n"
            + "        console.warn('TrustedTypes policy creation failed:', e);\n"
            + "    }\n"
            + "}\n"
            + "arguments[0].innerHTML = html;\n"
            + "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));";

    ((org.openqa.selenium.JavascriptExecutor) webDriver)
        .executeScript(script, inputArea, htmlPrompt);
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

    // Use WebDriverWait with a lambda expression to wait until aria-busy becomes "false" and text
    // is present
    WebDriverWait wait = new WebDriverWait(webDriver, GENERATION_TIMEOUT);
    return wait.until(
        driver -> {
          try {
            WebElement el = driver.findElement(responseBlockLocator);
            String ariaBusy = el.getAttribute("aria-busy");

            // Completion condition: aria-busy is "false" and content is not empty
            if ("false".equals(ariaBusy)) {
              el.getText();
              if (!el.getText().trim().isEmpty()) {
                return el;
              }
            }
          } catch (Exception e) {
            // Ignore exceptions while the element is being dynamically rendered on the DOM
          }
          return null;
        });
  }
}
