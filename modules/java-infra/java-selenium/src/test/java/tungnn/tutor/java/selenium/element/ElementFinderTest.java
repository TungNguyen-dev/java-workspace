package tungnn.tutor.java.selenium.element;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

class ElementFinderTest {

    private final SearchContext context = mock(SearchContext.class);
    private final By locator = By.cssSelector(".item");

    @Test
    void findElementIfPresentReturnsEmptyWhenNothingMatches() {
        when(context.findElements(locator)).thenReturn(List.of());

        assertThat(ElementFinder.findElementIfPresent(context, locator)).isEmpty();
    }

    @Test
    void findElementIfPresentReturnsFirstMatch() {
        WebElement first = mock(WebElement.class);
        WebElement second = mock(WebElement.class);
        when(context.findElements(locator)).thenReturn(List.of(first, second));

        assertThat(ElementFinder.findElementIfPresent(context, locator)).containsSame(first);
    }

    @Test
    void findElementAtRejectsOutOfRangeIndex() {
        when(context.findElements(locator)).thenReturn(List.of(mock(WebElement.class)));

        assertThatThrownBy(() -> ElementFinder.findElementAt(context, locator, 1))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("No element at index 1");
    }

    @Test
    void findFirstByExactTextMatchesTrimmedText() {
        WebElement a = mock(WebElement.class);
        WebElement b = mock(WebElement.class);
        when(a.getText()).thenReturn("  Alpha ");
        when(b.getText()).thenReturn("Beta");
        when(context.findElements(locator)).thenReturn(List.of(a, b));

        assertThat(ElementFinder.findFirstByExactText(context, locator, "Beta")).containsSame(b);
        assertThat(ElementFinder.findFirstByPartialText(context, locator, "lph"))
                .containsSame(a);
    }

    @Test
    void countElementsAndGetTexts() {
        WebElement a = mock(WebElement.class);
        when(a.getText()).thenReturn("x");
        when(context.findElements(locator)).thenReturn(List.of(a, a));

        assertThat(ElementFinder.countElements(context, locator)).isEqualTo(2);
        assertThat(ElementFinder.getTexts(context, locator)).containsExactly("x", "x");
    }

    @Test
    void nestedShadowRootRequiresSelectors() {
        assertThatThrownBy(() -> ElementFinder.findElementInNestedShadowRoot(mock(WebElement.class)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullLocatorIsRejected() {
        assertThatThrownBy(() -> ElementFinder.findElement(context, null)).isInstanceOf(NullPointerException.class);
    }
}
