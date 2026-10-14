package tungnn.tutor.java.aws.service.ssm;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;

public final class SsmComplianceUtil {

  private SsmComplianceUtil() {}

  public static List<ComplianceItem> listComplianceItems(
      SsmClient client, ListComplianceItemsRequest request) {
    return client.listComplianceItemsPaginator(request).stream()
        .flatMap(r -> r.complianceItems().stream())
        .collect(Collectors.toList());
  }

  public static List<ComplianceSummaryItem> listComplianceSummaries(
      SsmClient client, ListComplianceSummariesRequest request) {
    return client.listComplianceSummariesPaginator(request).stream()
        .flatMap(r -> r.complianceSummaryItems().stream())
        .collect(Collectors.toList());
  }

  public static List<ResourceComplianceSummaryItem> listResourceComplianceSummaries(
      SsmClient client, ListResourceComplianceSummariesRequest request) {
    return client.listResourceComplianceSummariesPaginator(request).stream()
        .flatMap(r -> r.resourceComplianceSummaryItems().stream())
        .collect(Collectors.toList());
  }

  public static PutComplianceItemsResponse putComplianceItems(
      SsmClient client, PutComplianceItemsRequest request) {
    return client.putComplianceItems(request);
  }
}
