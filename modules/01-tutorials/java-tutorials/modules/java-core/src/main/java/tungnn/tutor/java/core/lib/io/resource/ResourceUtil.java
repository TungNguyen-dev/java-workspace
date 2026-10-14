package tungnn.tutor.java.core.lib.io.resource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class ResourceUtil {

  private ResourceUtil() {}

  private static ClassLoader getClassLoader() {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    return (cl != null) ? cl : ResourceUtil.class.getClassLoader();
  }

  public static InputStream getResourceAsStream(String resourceName) {
    return getClassLoader().getResourceAsStream(resourceName);
  }

  public static String getResourceAsString(String resourceName) {
    try {
      Objects.requireNonNull(resourceName, "resourceName must not be null");

      StringBuilder builder = new StringBuilder();
      try (InputStream in = getResourceAsStream(resourceName);
          InputStreamReader isr = new InputStreamReader(in, StandardCharsets.UTF_8);
          BufferedReader br = new BufferedReader(isr)) {
        int b;
        while ((b = br.read()) != -1) {
          builder.append((char) b);
        }
      }
      return builder.toString();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
