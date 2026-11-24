package tungnn.tutor.java.selenium.browser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebDriver.TargetLocator;

class BrowserUtilsTest {

  private final WebDriver driver = mock(WebDriver.class);

  @Test
  void navigationDelegatesToDriver() {
    Navigation navigation = mock(Navigation.class);
    when(driver.navigate()).thenReturn(navigation);

    NavigationUtils.navigateTo(driver, "https://example.com");
    NavigationUtils.back(driver);
    NavigationUtils.forward(driver);
    NavigationUtils.refresh(driver);

    verify(navigation).to("https://example.com");
    verify(navigation).back();
    verify(navigation).forward();
    verify(navigation).refresh();
  }

  @Test
  void frameSwitchingDelegatesToTargetLocator() {
    TargetLocator locator = mock(TargetLocator.class);
    when(driver.switchTo()).thenReturn(locator);

    FrameUtils.switchToFrame(driver, 2);
    FrameUtils.switchToFrame(driver, "main");
    FrameUtils.switchToParentFrame(driver);
    FrameUtils.leaveFrame(driver);

    verify(locator).frame(2);
    verify(locator).frame("main");
    verify(locator).parentFrame();
    verify(locator).defaultContent();
  }

  @Test
  void cookiesDelegateToOptions() {
    Options options = mock(Options.class);
    Cookie cookie = new Cookie("a", "b");
    when(driver.manage()).thenReturn(options);
    when(options.getCookies()).thenReturn(Set.of(cookie));

    CookieUtils.addCookie(driver, cookie);
    CookieUtils.deleteCookieNamed(driver, "a");

    assertThat(CookieUtils.getAllCookies(driver)).containsExactly(cookie);
    verify(options).addCookie(cookie);
    verify(options).deleteCookieNamed("a");
  }

  @Test
  void strictAndLaxCookiesCarrySameSite() {
    Options options = mock(Options.class);
    when(driver.manage()).thenReturn(options);
    ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);

    CookieUtils.addStrictCookie(driver, "s", "1");
    CookieUtils.addLaxCookie(driver, "l", "2");

    verify(options, org.mockito.Mockito.times(2)).addCookie(captor.capture());
    assertThat(captor.getAllValues())
        .extracting(Cookie::getSameSite)
        .containsExactly("Strict", "Lax");
  }

  @Test
  void scriptUtilsDelegateToJavascriptExecutor() {
    WebDriver jsDriver = mock(WebDriver.class, org.mockito.Mockito.withSettings().extraInterfaces(JavascriptExecutor.class));
    when(((JavascriptExecutor) jsDriver).executeScript("return 1", 7)).thenReturn(1L);

    assertThat(ScriptUtils.executeScript(jsDriver, "return 1", 7)).isEqualTo(1L);
  }
}
