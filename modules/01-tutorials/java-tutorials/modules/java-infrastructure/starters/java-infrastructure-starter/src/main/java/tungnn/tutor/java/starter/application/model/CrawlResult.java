package tungnn.tutor.java.starter.application.model;

import java.net.URI;
import java.nio.file.Path;

public record CrawlResult(URI url, Path outputFile, Status status, String errorMessage) {

  public static CrawlResult success(URI url, Path outputFile) {
    return new CrawlResult(url, outputFile, Status.SUCCESS, null);
  }

  public static CrawlResult failed(URI url, String errorMessage) {
    return new CrawlResult(url, null, Status.FAILED, errorMessage);
  }

  public static CrawlResult skipped(URI url, String reason) {
    return new CrawlResult(url, null, Status.SKIPPED, reason);
  }

  public boolean isSuccess() {
    return status == Status.SUCCESS;
  }

  public boolean isSkipped() {
    return status == Status.SKIPPED;
  }

  public boolean isFailed() {
    return status == Status.FAILED;
  }

  public enum Status {
    SUCCESS,
    FAILED,
    SKIPPED
  }
}
