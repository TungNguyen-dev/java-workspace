package tungnn.tutor.java.spring.tool.doctrans.app.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tungnn.tutor.java.spring.tool.doctrans.app.config.DocTransConfig;
import tungnn.tutor.java.spring.tool.doctrans.app.exception.ApiException;
import tungnn.tutor.java.spring.tool.doctrans.app.model.JobStatus;
import tungnn.tutor.java.spring.tool.doctrans.app.model.TranslationJob;
import tungnn.tutor.java.spring.tool.doctrans.app.repository.InMemoryJobRepository;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTranslationErrorCode;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTranslationResult;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;
import tungnn.tutor.java.tool.doctrans.core.service.DocumentTranslationService;

@Service
public class TranslationJobService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TranslationJobService.class);
  private static final Set<String> SUPPORTED_EXTENSIONS =
      Set.of("docx", "xlsx", "pptx", "md", "txt");

  private final DocTransConfig config;
  private final DocumentTranslationService translationService;
  private final InMemoryJobRepository repository;
  private final ExecutorService executor;

  public TranslationJobService(
      DocTransConfig config,
      DocumentTranslationService translationService,
      InMemoryJobRepository repository,
      @Qualifier("translationJobExecutor") ExecutorService executor) {
    this.config = config;
    this.translationService = translationService;
    this.repository = repository;
    this.executor = executor;
  }

  private static String extensionOf(String filename) {
    int dot = filename.lastIndexOf('.');
    return dot < 0 ? "" : filename.substring(dot + 1).toLowerCase();
  }

  private static ApiException badRequest(String code, String message) {
    return new ApiException(HttpStatus.BAD_REQUEST, code, message);
  }

  public TranslationJob submit(MultipartFile file, String targetLanguage) {
    var language = parseLanguage(targetLanguage);
    if (file == null || file.isEmpty()) {
      throw badRequest("INVALID_FILE", "File must not be empty");
    }
    var originalName = file.getOriginalFilename() == null ? "document" : file.getOriginalFilename();
    var extension = extensionOf(originalName);
    if (!SUPPORTED_EXTENSIONS.contains(extension)) {
      throw badRequest(
          "UNSUPPORTED_FORMAT",
          "Supported formats: " + SUPPORTED_EXTENSIONS + ", got: ." + extension);
    }

    var jobId = UUID.randomUUID().toString();
    var source = config.jobDir().resolve(jobId).resolve("source." + extension);
    try {
      Files.createDirectories(source.getParent());
      file.transferTo(source);
    } catch (IOException e) {
      throw new ApiException(
          HttpStatus.INTERNAL_SERVER_ERROR, "STORAGE_ERROR", "Failed to store uploaded file");
    }

    var job = TranslationJob.pending(jobId, originalName, language.name(), source);
    repository.save(job);
    executor.execute(() -> run(jobId));
    LOGGER.info("Job [{}] submitted: file={}, lang={}", jobId, originalName, language);
    return job;
  }

  public TranslationJob get(String jobId) {
    return repository
        .find(jobId)
        .orElseThrow(
            () ->
                new ApiException(HttpStatus.NOT_FOUND, "JOB_NOT_FOUND", "Job not found: " + jobId));
  }

  public Path resultOf(TranslationJob job) {
    if (job.status() != JobStatus.COMPLETED) {
      throw new ApiException(
          HttpStatus.CONFLICT,
          "JOB_NOT_COMPLETED",
          "Job is " + job.status() + ", result is not available");
    }
    if (job.resultPath() == null || Files.notExists(job.resultPath())) {
      throw new ApiException(
          HttpStatus.NOT_FOUND, "RESULT_NOT_FOUND", "Translated file no longer exists");
    }
    return job.resultPath();
  }

  public String downloadFilename(TranslationJob job) {
    var name = job.originalFilename();
    int dot = name.lastIndexOf('.');
    var base = dot > 0 ? name.substring(0, dot) : name;
    var ext = dot > 0 ? name.substring(dot) : "";

    var countryName =
        LanguageCode.valueOf(job.targetLanguage()).countryCode().replaceAll("\\s+", "-");
    var timestamp = job.finishedAt().getEpochSecond();

    return base + "_" + countryName + "_" + timestamp + ext;
  }

  private void run(String jobId) {
    repository.update(jobId, TranslationJob::markProcessing);
    var job = repository.find(jobId).orElseThrow();
    try {
      var result = translationService.translateDocument(job.sourcePath(), job.targetLanguage());
      switch (result) {
        case DocumentTranslationResult.Success success -> {
          repository.update(jobId, j -> j.markCompleted(success.translation().path()));
          LOGGER.info("Job [{}] completed", jobId);
        }
        case DocumentTranslationResult.Failure failure -> {
          var err = failure.error();
          var code =
              (err.code() == null || err.code().isBlank())
                  ? DocumentTranslationErrorCode.TRANSLATION_FAILED.code()
                  : err.code();
          repository.update(jobId, j -> j.markFailed(code, err.message()));
          LOGGER.error("Job [{}] failed: {}", jobId, err.message());
        }
      }
    } catch (Exception e) {
      LOGGER.error("Job [{}] crashed", jobId, e);
      repository.update(
          jobId,
          j -> j.markFailed(DocumentTranslationErrorCode.UNKNOWN_ERROR.code(), e.getMessage()));
    }
  }

  private LanguageCode parseLanguage(String value) {
    try {
      return LanguageCode.valueOf(value.trim().toUpperCase());
    } catch (Exception e) {
      throw badRequest(
          "INVALID_LANGUAGE",
          "targetLanguage must be one of " + Arrays.toString(LanguageCode.values()));
    }
  }
}
