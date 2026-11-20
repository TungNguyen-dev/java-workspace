package tungnn.tutor.java.aws.service.cloudformation;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.cloudformation.CloudFormationClient;
import software.amazon.awssdk.services.cloudformation.model.*;

public final class CfnTemplateUtil {
  private CfnTemplateUtil() {}

  public static CreateGeneratedTemplateResponse createGeneratedTemplate(
      CloudFormationClient client, CreateGeneratedTemplateRequest request) {
    return client.createGeneratedTemplate(request);
  }

  public static DeleteGeneratedTemplateResponse deleteGeneratedTemplate(
      CloudFormationClient client, DeleteGeneratedTemplateRequest request) {
    return client.deleteGeneratedTemplate(request);
  }

  public static DescribeGeneratedTemplateResponse describeGeneratedTemplate(
      CloudFormationClient client, DescribeGeneratedTemplateRequest request) {
    return client.describeGeneratedTemplate(request);
  }

  public static DescribeResourceScanResponse describeResourceScan(
      CloudFormationClient client, DescribeResourceScanRequest request) {
    return client.describeResourceScan(request);
  }

  public static EstimateTemplateCostResponse estimateTemplateCost(
      CloudFormationClient client, EstimateTemplateCostRequest request) {
    return client.estimateTemplateCost(request);
  }

  public static GetGeneratedTemplateResponse getGeneratedTemplate(
      CloudFormationClient client, GetGeneratedTemplateRequest request) {
    return client.getGeneratedTemplate(request);
  }

  public static GetTemplateResponse getTemplate(
      CloudFormationClient client, GetTemplateRequest request) {
    return client.getTemplate(request);
  }

  public static GetTemplateSummaryResponse getTemplateSummary(
      CloudFormationClient client, GetTemplateSummaryRequest request) {
    return client.getTemplateSummary(request);
  }

  public static List<TemplateSummary> listGeneratedTemplates(
      CloudFormationClient client, ListGeneratedTemplatesRequest request) {
    return client.listGeneratedTemplatesPaginator(request).stream()
        .flatMap(r -> r.summaries().stream())
        .collect(Collectors.toList());
  }

  public static List<ScannedResource> listResourceScanRelatedResources(
      CloudFormationClient client, ListResourceScanRelatedResourcesRequest request) {
    return client.listResourceScanRelatedResourcesPaginator(request).stream()
        .flatMap(r -> r.relatedResources().stream())
        .collect(Collectors.toList());
  }

  public static List<ScannedResource> listResourceScanResources(
      CloudFormationClient client, ListResourceScanResourcesRequest request) {
    return client.listResourceScanResourcesPaginator(request).stream()
        .flatMap(r -> r.resources().stream())
        .collect(Collectors.toList());
  }

  public static List<ResourceScanSummary> listResourceScans(
      CloudFormationClient client, ListResourceScansRequest request) {
    return client.listResourceScansPaginator(request).stream()
        .flatMap(r -> r.resourceScanSummaries().stream())
        .collect(Collectors.toList());
  }

  public static StartResourceScanResponse startResourceScan(
      CloudFormationClient client, StartResourceScanRequest request) {
    return client.startResourceScan(request);
  }

  public static UpdateGeneratedTemplateResponse updateGeneratedTemplate(
      CloudFormationClient client, UpdateGeneratedTemplateRequest request) {
    return client.updateGeneratedTemplate(request);
  }

  public static ValidateTemplateResponse validateTemplate(
      CloudFormationClient client, ValidateTemplateRequest request) {
    return client.validateTemplate(request);
  }
}
