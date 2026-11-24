package tungnn.tutor.java.selenium.browser;

import java.util.Set;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

public final class CookieUtils {

  private CookieUtils() {}

  public static void addCookie(WebDriver driver, Cookie cookie) {
    driver.manage().addCookie(cookie);
  }

  public static Cookie getCookieNamed(WebDriver driver, String name) {
    return driver.manage().getCookieNamed(name);
  }

  public static Set<Cookie> getAllCookies(WebDriver driver) {
    return driver.manage().getCookies();
  }

  public static void deleteCookie(WebDriver driver, Cookie cookie) {
    driver.manage().deleteCookie(cookie);
  }

  public static void deleteCookieNamed(WebDriver driver, String name) {
    driver.manage().deleteCookieNamed(name);
  }

  public static void deleteAllCookies(WebDriver driver) {
    driver.manage().deleteAllCookies();
  }

  public static void addSameSiteCookie(
      WebDriver driver, String name, String value, String domain, String path, String sameSite) {
    Cookie cookie =
        new Cookie.Builder(name, value)
            .domain(domain)
            .path(path)
            .sameSite(sameSite) // Strict or Lax
            .build();
    driver.manage().addCookie(cookie);
  }

  public static void addStrictCookie(WebDriver driver, String name, String value) {
    Cookie cookie = new Cookie.Builder(name, value).sameSite("Strict").build();
    driver.manage().addCookie(cookie);
  }

  public static void addLaxCookie(WebDriver driver, String name, String value) {
    Cookie cookie = new Cookie.Builder(name, value).sameSite("Lax").build();
    driver.manage().addCookie(cookie);
  }
}
