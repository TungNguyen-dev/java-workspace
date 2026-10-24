package tungnn.tutor.java.spring.tool.doctrans.app.controller;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tungnn.tutor.java.spring.tool.doctrans.app.model.JobResponse;
import tungnn.tutor.java.spring.tool.doctrans.app.service.TranslationJobService;

@RestController
@RequestMapping("/api/v1/translation-jobs")
public class TranslationJobController {

  private final TranslationJobService jobService;

  public TranslationJobController(TranslationJobService jobService) {
    this.jobService = jobService;
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<JobResponse> create(
      @RequestParam("file") MultipartFile file,
      @RequestParam("targetLanguage") String targetLanguage) {
    var job = jobService.submit(file, targetLanguage);
    return ResponseEntity.accepted()
        .location(URI.create("/api/v1/translation-jobs/" + job.id()))
        .body(JobResponse.from(job));
  }

  @GetMapping("/{jobId}")
  public JobResponse getStatus(@PathVariable String jobId) {
    return JobResponse.from(jobService.get(jobId));
  }

  @GetMapping("/{jobId}/download")
  public ResponseEntity<Resource> download(@PathVariable String jobId) {
    var job = jobService.get(jobId);
    var path = jobService.resultOf(job);
    var disposition =
        ContentDisposition.attachment()
            .filename(jobService.downloadFilename(job), StandardCharsets.UTF_8)
            .build();
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
        .body(new FileSystemResource(path));
  }
}
