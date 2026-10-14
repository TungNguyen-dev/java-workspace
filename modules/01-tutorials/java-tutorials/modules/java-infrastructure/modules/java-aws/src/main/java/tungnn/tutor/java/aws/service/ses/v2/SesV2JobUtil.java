// SesV2JobUtil.java
package tungnn.tutor.java.aws.service.ses.v2;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

public final class SesV2JobUtil {

  private SesV2JobUtil() {}

  public static CancelExportJobResponse cancelExportJob(
      SesV2Client client, CancelExportJobRequest request) {
    return client.cancelExportJob(request);
  }

  public static CreateExportJobResponse createExportJob(
      SesV2Client client, CreateExportJobRequest request) {
    return client.createExportJob(request);
  }

  public static CreateImportJobResponse createImportJob(
      SesV2Client client, CreateImportJobRequest request) {
    return client.createImportJob(request);
  }

  public static GetExportJobResponse getExportJob(SesV2Client client, GetExportJobRequest request) {
    return client.getExportJob(request);
  }

  public static GetImportJobResponse getImportJob(SesV2Client client, GetImportJobRequest request) {
    return client.getImportJob(request);
  }

  public static List<ExportJobSummary> listExportJobs(
      SesV2Client client, ListExportJobsRequest request) {
    return client.listExportJobsPaginator(request).stream()
        .flatMap(r -> r.exportJobs().stream())
        .collect(Collectors.toList());
  }

  public static List<ImportJobSummary> listImportJobs(
      SesV2Client client, ListImportJobsRequest request) {
    return client.listImportJobsPaginator(request).stream()
        .flatMap(r -> r.importJobs().stream())
        .collect(Collectors.toList());
  }
}
