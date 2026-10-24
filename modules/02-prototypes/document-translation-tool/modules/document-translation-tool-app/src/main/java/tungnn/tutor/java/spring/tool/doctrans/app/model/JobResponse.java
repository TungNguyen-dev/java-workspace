package tungnn.tutor.java.spring.tool.doctrans.app.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record JobResponse(
    String jobId,
    JobStatus status,
    String originalFilename,
    String targetLanguage,
    Instant createdAt,
    Instant startedAt,
    Instant finishedAt,
    ErrorResponse error,
    String downloadUrl) {

  public static JobResponse from(TranslationJob job) {
    var error =
        job.status() == JobStatus.FAILED
            ? new ErrorResponse(job.errorCode(), job.errorMessage())
            : null;
    var downloadUrl =
        job.status() == JobStatus.COMPLETED
            ? "/api/v1/translation-jobs/" + job.id() + "/download"
            : null;
    return new JobResponse(
        job.id(),
        job.status(),
        job.originalFilename(),
        job.targetLanguage(),
        job.createdAt(),
        job.startedAt(),
        job.finishedAt(),
        error,
        downloadUrl);
  }
}
