package tungnn.tutor.java.spring.tool.doctrans.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tungnn.tutor.java.spring.tool.doctrans.app.config.DocTransConfig;
import tungnn.tutor.java.spring.tool.doctrans.app.model.TranslateDocumentRequest;
import tungnn.tutor.java.spring.tool.doctrans.app.model.TranslateDocumentResult;
import tungnn.tutor.java.spring.tool.doctrans.app.service.DocumentTranslationService;

@Component
public class DocumentTranslationRunner implements CommandLineRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(DocumentTranslationRunner.class);

  private final DocumentTranslationService translationService;
  private final DocTransConfig docTransConfig;
  private final ApplicationArguments applicationArguments;

  public DocumentTranslationRunner(
      DocumentTranslationService translationService,
      DocTransConfig docTransConfig,
      ApplicationArguments applicationArguments) {
    this.translationService = translationService;
    this.docTransConfig = docTransConfig;
    this.applicationArguments = applicationArguments;
  }

  @Override
  public void run(String @NonNull ... args) {
    LOGGER.info("Loading document translation configuration...");
    ensureDirectoriesExist(docTransConfig);

    var inputFiles = scanInputFiles(docTransConfig.inputDir());
    if (inputFiles.isEmpty()) {
      LOGGER.info("No document files found in input directory: {}", docTransConfig.inputDir());
      return;
    }

    LOGGER.info("Found {} document file(s) to translate.", inputFiles.size());

    try {
      // 1. Lấy target language ưu tiên từ arguments, nếu không có lấy từ config/yaml
      String targetLanguage = args[0];

      var documents =
          inputFiles.stream()
              .map(
                  filePath -> {
                    var relativePath = docTransConfig.inputDir().relativize(filePath);
                    return new TranslateDocumentRequest.Document(filePath, relativePath);
                  })
              .collect(Collectors.toSet());

      // 2. Truyền target language vào request
      var request = new TranslateDocumentRequest(documents, targetLanguage);

      LOGGER.info(
          "Starting sequential document translation process (Target Language: {})...",
          targetLanguage);
      var result = translationService.translateDocuments(request);

      LOGGER.info("Moving successfully processed document files to the 'done' directory...");
      moveSuccessfulFilesToDone(result, docTransConfig.inputDir(), docTransConfig.doneDir());

      LOGGER.info("Document translation and storage process completed successfully!");

    } catch (Exception e) {
      LOGGER.error("Fatal error during DocumentTranslationRunner execution: {}", e.getMessage(), e);
    }
  }

  private String resolveTargetLanguage() {
    if (applicationArguments.containsOption("target-language")) {
      var values = applicationArguments.getOptionValues("target-language");
      if (values != null && !values.isEmpty()) {
        return values.get(0);
      }
    }
    if (applicationArguments.containsOption("doctrans.target-language")) {
      var values = applicationArguments.getOptionValues("doctrans.target-language");
      if (values != null && !values.isEmpty()) {
        return values.get(0);
      }
    }
    return docTransConfig.targetLanguage();
  }

  private void ensureDirectoriesExist(DocTransConfig config) {
    try {
      Files.createDirectories(config.inputDir());
      Files.createDirectories(config.outputDir());
      Files.createDirectories(config.doneDir());
    } catch (IOException e) {
      throw new RuntimeException("Failed to create storage directories", e);
    }
  }

  private List<Path> scanInputFiles(Path inputDir) {
    try (var stream = Files.walk(inputDir)) {
      return stream
          .filter(Files::isRegularFile)
          .filter(
              p -> {
                try {
                  return !Files.isHidden(p);
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              })
          .toList();
    } catch (IOException e) {
      LOGGER.error("Error scanning input directory: {}", e.getMessage(), e);
      return List.of();
    }
  }

  private void moveSuccessfulFilesToDone(
      TranslateDocumentResult result, Path inputDir, Path doneDir) {
    if (result == null || result.documents() == null) {
      return;
    }

    for (var docRes : result.documents()) {
      if (!docRes.success()) {
        LOGGER.warn(
            "Document translation failed or encountered errors, retaining input file: {}",
            docRes.relativePath());
        if (docRes.error() != null) {
          LOGGER.error("Translation error details:", docRes.error());
        }
        continue;
      }

      var relativePath = docRes.relativePath();
      var sourcePath = inputDir.resolve(relativePath);
      var targetPath = doneDir.resolve(relativePath);

      if (!Files.exists(sourcePath)) {
        LOGGER.error("Source file not found for moving: {}", sourcePath);
        continue;
      }

      try {
        if (targetPath.getParent() != null) {
          Files.createDirectories(targetPath.getParent());
        }
        Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        LOGGER.info("Moved successfully translated file to done directory: {}", targetPath);
      } catch (IOException e) {
        LOGGER.error("Failed to move file {}: {}", sourcePath, e.getMessage(), e);
      }
    }
  }
}
