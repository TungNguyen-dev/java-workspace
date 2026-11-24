package tungnn.tutor.java.selenium.element;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

class ElementInfoTest {

    private final WebElement element = mock(WebElement.class);

    @Test
    void getTextTrimsAndHandlesNullAndStale() {
        when(element.getText()).thenReturn("  hi  ");
        assertThat(ElementInfo.getText(element)).isEqualTo("hi");

        assertThat(ElementInfo.getText((WebElement) null)).isEmpty();

        WebElement stale = mock(WebElement.class);
        when(stale.getText()).thenThrow(new StaleElementReferenceException("stale"));
        assertThat(ElementInfo.getText(stale)).isEmpty();
    }

    @Test
    void stateChecksReturnFalseForNullAndStaleElements() {
        WebElement stale = mock(WebElement.class);
        when(stale.isDisplayed()).thenThrow(new StaleElementReferenceException("stale"));
        when(stale.isEnabled()).thenThrow(new StaleElementReferenceException("stale"));
        when(stale.isSelected()).thenThrow(new StaleElementReferenceException("stale"));

        assertThat(ElementInfo.isDisplayed(stale)).isFalse();
        assertThat(ElementInfo.isEnabled(stale)).isFalse();
        assertThat(ElementInfo.isSelected(stale)).isFalse();
        assertThat(ElementInfo.isDisplayed((WebElement) null)).isFalse();
    }

    @Test
    void stateChecksDelegateToElement() {
        when(element.isDisplayed()).thenReturn(true);
        when(element.isEnabled()).thenReturn(true);
        when(element.isSelected()).thenReturn(true);

        assertThat(ElementInfo.isDisplayed(element)).isTrue();
        assertThat(ElementInfo.isEnabled(element)).isTrue();
        assertThat(ElementInfo.isSelected(element)).isTrue();
    }

    @Test
    void locatorVariantsReturnFalseWhenAbsent() {
        SearchContext context = mock(SearchContext.class);
        By locator = By.id("missing");
        when(context.findElements(locator)).thenReturn(List.of());

        assertThat(ElementInfo.isPresent(context, locator)).isFalse();
        assertThat(ElementInfo.isDisplayed(context, locator)).isFalse();
        assertThat(ElementInfo.getText(context, locator)).isEmpty();
    }

    @Test
    void getValueReadsDomProperty() {
        when(element.getDomProperty("value")).thenReturn("v");

        assertThat(ElementInfo.getValue(element)).isEqualTo("v");
    }

    @Test
    void unwrapDriverRejectsPlainElement() {
        assertThatThrownBy(() -> ElementInfo.unwrapDriver(element))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("Cannot resolve WebDriver");
    }
}
