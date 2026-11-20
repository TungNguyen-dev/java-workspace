package tungnn.tutor.java.aws.service.dynamodb;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

public final class DynamoTableUtil {
  private DynamoTableUtil() {}

  public static CreateTableResponse createTable(DynamoDbClient client, CreateTableRequest request) {
    return client.createTable(request);
  }

  public static DeleteTableResponse deleteTable(DynamoDbClient client, DeleteTableRequest request) {
    return client.deleteTable(request);
  }

  public static DescribeTableResponse describeTable(
      DynamoDbClient client, DescribeTableRequest request) {
    return client.describeTable(request);
  }

  public static UpdateTableResponse updateTable(DynamoDbClient client, UpdateTableRequest request) {
    return client.updateTable(request);
  }

  public static List<String> listTables(DynamoDbClient client, ListTablesRequest request) {
    return client.listTablesPaginator(request).stream()
        .flatMap(r -> r.tableNames().stream())
        .collect(Collectors.toList());
  }

  public static DescribeTimeToLiveResponse describeTimeToLive(
      DynamoDbClient client, DescribeTimeToLiveRequest request) {
    return client.describeTimeToLive(request);
  }

  public static UpdateTimeToLiveResponse updateTimeToLive(
      DynamoDbClient client, UpdateTimeToLiveRequest request) {
    return client.updateTimeToLive(request);
  }

  public static DescribeLimitsResponse describeLimits(
      DynamoDbClient client, DescribeLimitsRequest request) {
    return client.describeLimits(request);
  }

  public static DescribeEndpointsResponse describeEndpoints(
      DynamoDbClient client, DescribeEndpointsRequest request) {
    return client.describeEndpoints(request);
  }

  public static DescribeTableReplicaAutoScalingResponse describeTableReplicaAutoScaling(
      DynamoDbClient client, DescribeTableReplicaAutoScalingRequest request) {
    return client.describeTableReplicaAutoScaling(request);
  }

  public static UpdateTableReplicaAutoScalingResponse updateTableReplicaAutoScaling(
      DynamoDbClient client, UpdateTableReplicaAutoScalingRequest request) {
    return client.updateTableReplicaAutoScaling(request);
  }

  public static CreateGlobalTableResponse createGlobalTable(
      DynamoDbClient client, CreateGlobalTableRequest request) {
    return client.createGlobalTable(request);
  }

  public static DescribeGlobalTableResponse describeGlobalTable(
      DynamoDbClient client, DescribeGlobalTableRequest request) {
    return client.describeGlobalTable(request);
  }

  public static DescribeGlobalTableSettingsResponse describeGlobalTableSettings(
      DynamoDbClient client, DescribeGlobalTableSettingsRequest request) {
    return client.describeGlobalTableSettings(request);
  }

  public static ListGlobalTablesResponse listGlobalTables(
      DynamoDbClient client, ListGlobalTablesRequest request) {
    return client.listGlobalTables(request);
  }

  public static UpdateGlobalTableResponse updateGlobalTable(
      DynamoDbClient client, UpdateGlobalTableRequest request) {
    return client.updateGlobalTable(request);
  }

  public static UpdateGlobalTableSettingsResponse updateGlobalTableSettings(
      DynamoDbClient client, UpdateGlobalTableSettingsRequest request) {
    return client.updateGlobalTableSettings(request);
  }

  public static DescribeContributorInsightsResponse describeContributorInsights(
      DynamoDbClient client, DescribeContributorInsightsRequest request) {
    return client.describeContributorInsights(request);
  }

  public static List<ContributorInsightsSummary> listContributorInsights(
      DynamoDbClient client, ListContributorInsightsRequest request) {
    return client.listContributorInsightsPaginator(request).stream()
        .flatMap(r -> r.contributorInsightsSummaries().stream())
        .collect(Collectors.toList());
  }

  public static UpdateContributorInsightsResponse updateContributorInsights(
      DynamoDbClient client, UpdateContributorInsightsRequest request) {
    return client.updateContributorInsights(request);
  }

  public static DescribeKinesisStreamingDestinationResponse describeKinesisStreamingDestination(
      DynamoDbClient client, DescribeKinesisStreamingDestinationRequest request) {
    return client.describeKinesisStreamingDestination(request);
  }

  public static EnableKinesisStreamingDestinationResponse enableKinesisStreamingDestination(
      DynamoDbClient client, EnableKinesisStreamingDestinationRequest request) {
    return client.enableKinesisStreamingDestination(request);
  }

  public static DisableKinesisStreamingDestinationResponse disableKinesisStreamingDestination(
      DynamoDbClient client, DisableKinesisStreamingDestinationRequest request) {
    return client.disableKinesisStreamingDestination(request);
  }

  public static UpdateKinesisStreamingDestinationResponse updateKinesisStreamingDestination(
      DynamoDbClient client, UpdateKinesisStreamingDestinationRequest request) {
    return client.updateKinesisStreamingDestination(request);
  }

  public static GetResourcePolicyResponse getResourcePolicy(
      DynamoDbClient client, GetResourcePolicyRequest request) {
    return client.getResourcePolicy(request);
  }

  public static PutResourcePolicyResponse putResourcePolicy(
      DynamoDbClient client, PutResourcePolicyRequest request) {
    return client.putResourcePolicy(request);
  }

  public static DeleteResourcePolicyResponse deleteResourcePolicy(
      DynamoDbClient client, DeleteResourcePolicyRequest request) {
    return client.deleteResourcePolicy(request);
  }

  public static ExportTableToPointInTimeResponse exportTableToPointInTime(
      DynamoDbClient client, ExportTableToPointInTimeRequest request) {
    return client.exportTableToPointInTime(request);
  }

  public static DescribeExportResponse describeExport(
      DynamoDbClient client, DescribeExportRequest request) {
    return client.describeExport(request);
  }

  public static List<ExportSummary> listExports(DynamoDbClient client, ListExportsRequest request) {
    return client.listExportsPaginator(request).stream()
        .flatMap(r -> r.exportSummaries().stream())
        .collect(Collectors.toList());
  }

  public static ImportTableResponse importTable(DynamoDbClient client, ImportTableRequest request) {
    return client.importTable(request);
  }

  public static DescribeImportResponse describeImport(
      DynamoDbClient client, DescribeImportRequest request) {
    return client.describeImport(request);
  }

  public static List<ImportSummary> listImports(DynamoDbClient client, ListImportsRequest request) {
    return client.listImportsPaginator(request).stream()
        .flatMap(r -> r.importSummaryList().stream())
        .collect(Collectors.toList());
  }
}
