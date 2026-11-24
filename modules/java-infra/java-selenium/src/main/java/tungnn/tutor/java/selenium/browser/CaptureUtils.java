package tungnn.tutor.java.selenium.browser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Pdf;
import org.openqa.selenium.PrintsPage;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.print.PrintOptions;

public final class CaptureUtils {

  private CaptureUtils() {}

  public static File takeScreenshot(WebDriver driver, String destinationPath) throws IOException {
    TakesScreenshot ts = (TakesScreenshot) driver;
    File source = ts.getScreenshotAs(OutputType.FILE);
    File destination = new File(destinationPath);

    // Create the parent directory if it does not exist
    if (destination.getParentFile() != null) {
      destination.getParentFile().mkdirs();
    }

    Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
    return destination;
  }

  public static File takeElementScreenshot(WebElement element, String destinationPath)
      throws IOException {
    File source = element.getScreenshotAs(OutputType.FILE);
    File destination = new File(destinationPath);

    if (destination.getParentFile() != null) {
      destination.getParentFile().mkdirs();
    }

    Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
    return destination;
  }

  public static File printPage(WebDriver driver, String destinationPdfPath) throws IOException {
    PrintsPage printer = (PrintsPage) driver;
    PrintOptions printOptions = new PrintOptions();

    Pdf pdf = printer.print(printOptions);
    byte[] pdfBytes = java.util.Base64.getDecoder().decode(pdf.getContent());

    File destination = new File(destinationPdfPath);
    if (destination.getParentFile() != null) {
      destination.getParentFile().mkdirs();
    }

    Files.write(destination.toPath(), pdfBytes);
    return destination;
  }
}
