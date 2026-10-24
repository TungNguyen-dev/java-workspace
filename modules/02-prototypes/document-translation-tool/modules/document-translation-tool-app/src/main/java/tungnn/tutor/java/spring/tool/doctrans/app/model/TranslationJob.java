package tungnn.tutor.java.spring.tool.doctrans.app.model;

import java.nio.file.Path;
import java.time.Instant;

public record TranslationJob(
    String id,
    String originalFilename,
    String targetLanguage,
    JobStatus status,
    Path sourcePath,
    Path resultPath,
    String errorCode,
    String errorMessage,
    Instant createdAt,
    Instant startedAt,
    Instant finishedAt) {

  public static TranslationJob pending(
      String id, String originalFilename, String targetLanguage, Path sourcePath) {
    return new TranslationJob(
        id,
        originalFilename,
        targetLanguage,
        JobStatus.PENDING,
        sourcePath,
        null,
        null,
        null,
        Instant.now(),
        null,
        null);
  }

  public TranslationJob markProcessing() {
    return new TranslationJob(
        id,
        originalFilename,
        targetLanguage,
        JobStatus.PROCESSING,
        sourcePath,
        null,
        null,
        null,
        createdAt,
        Instant.now(),
        null);
  }

  public TranslationJob markCompleted(Path result) {
    return new TranslationJob(
        id,
        originalFilename,
        targetLanguage,
        JobStatus.COMPLETED,
        sourcePath,
        result,
        null,
        null,
        createdAt,
        startedAt,
        Instant.now());
  }

  public TranslationJob markFailed(String code, String message) {
    return new TranslationJob(
        id,
        originalFilename,
        targetLanguage,
        JobStatus.FAILED,
        sourcePath,
        null,
        code,
        message,
        createdAt,
        startedAt,
        Instant.now());
  }
}
