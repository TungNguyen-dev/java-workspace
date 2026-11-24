package tungnn.tutor.java.selenium.element;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

class ElementActionsTest {

    private final WebElement element = mock(WebElement.class);

    @Test
    void typeClearsBeforeSendingKeys() {
        ElementActions.type(element, "text");

        InOrder order = inOrder(element);
        order.verify(element).clear();
        order.verify(element).sendKeys("text");
    }

    @Test
    void clickDelegatesToElement() {
        ElementActions.click(element);

        verify(element).click();
    }

    @Test
    void submitClicksDefaultSubmitButton() {
        WebElement button = mock(WebElement.class);
        when(element.findElement(any(By.class))).thenReturn(button);

        ElementActions.submit(element);

        verify(button).click();
    }

    @Test
    void uploadFileRejectsMissingFile(@TempDir Path dir) {
        assertThatThrownBy(() -> ElementActions.uploadFile(element, dir.resolve("none.txt")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("File does not exist");
    }

    @Test
    void uploadFileRejectsNonFileInput(@TempDir Path dir) throws IOException {
        Path file = Files.writeString(dir.resolve("a.txt"), "x");
        when(element.getTagName()).thenReturn("div");

        assertThatThrownBy(() -> ElementActions.uploadFile(element, file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("<input type=\"file\">");
    }

    @Test
    void uploadFileSendsAbsolutePath(@TempDir Path dir) throws IOException {
        Path file = Files.writeString(dir.resolve("a.txt"), "x");
        when(element.getTagName()).thenReturn("input");
        when(element.getDomAttribute("type")).thenReturn("file");

        ElementActions.uploadFile(element, file);

        verify(element).sendKeys(file.toAbsolutePath().normalize().toString());
    }

    @Test
    void uploadFilesJoinsPathsWithNewline(@TempDir Path dir) {
        Path a = dir.resolve("a");
        Path b = dir.resolve("b");

        ElementActions.uploadFiles(element, List.of(a, b));

        verify(element).sendKeys(a.toAbsolutePath() + "\n" + b.toAbsolutePath());
    }

    @Test
    void jsClickRequiresJavascriptExecutor() {
        WebDriver plain = mock(WebDriver.class);

        assertThatThrownBy(() -> ElementActions.jsClick(plain, element))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("JavascriptExecutor");
    }
}
