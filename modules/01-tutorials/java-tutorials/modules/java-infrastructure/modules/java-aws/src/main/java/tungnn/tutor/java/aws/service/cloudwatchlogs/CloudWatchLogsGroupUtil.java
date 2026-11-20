package tungnn.tutor.java.aws.service.cloudwatchlogs;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.*;

public final class CloudWatchLogsGroupUtil {
  private CloudWatchLogsGroupUtil() {}

  public static AssociateKmsKeyResponse associateKmsKey(
      CloudWatchLogsClient client, AssociateKmsKeyRequest request) {
    return client.associateKmsKey(request);
  }

  public static AssociateSourceToS3TableIntegrationResponse associateSourceToS3TableIntegration(
      CloudWatchLogsClient client, AssociateSourceToS3TableIntegrationRequest request) {
    return client.associateSourceToS3TableIntegration(request);
  }

  public static CancelExportTaskResponse cancelExportTask(
      CloudWatchLogsClient client, CancelExportTaskRequest request) {
    return client.cancelExportTask(request);
  }

  public static CancelImportTaskResponse cancelImportTask(
      CloudWatchLogsClient client, CancelImportTaskRequest request) {
    return client.cancelImportTask(request);
  }

  public static CreateExportTaskResponse createExportTask(
      CloudWatchLogsClient client, CreateExportTaskRequest request) {
    return client.createExportTask(request);
  }

  public static CreateImportTaskResponse createImportTask(
      CloudWatchLogsClient client, CreateImportTaskRequest request) {
    return client.createImportTask(request);
  }

  public static CreateLogGroupResponse createLogGroup(
      CloudWatchLogsClient client, CreateLogGroupRequest request) {
    return client.createLogGroup(request);
  }

  public static CreateLookupTableResponse createLookupTable(
      CloudWatchLogsClient client, CreateLookupTableRequest request) {
    return client.createLookupTable(request);
  }

  public static DeleteAccountPolicyResponse deleteAccountPolicy(
      CloudWatchLogsClient client, DeleteAccountPolicyRequest request) {
    return client.deleteAccountPolicy(request);
  }

  public static DeleteDataProtectionPolicyResponse deleteDataProtectionPolicy(
      CloudWatchLogsClient client, DeleteDataProtectionPolicyRequest request) {
    return client.deleteDataProtectionPolicy(request);
  }

  public static DeleteIndexPolicyResponse deleteIndexPolicy(
      CloudWatchLogsClient client, DeleteIndexPolicyRequest request) {
    return client.deleteIndexPolicy(request);
  }

  public static DeleteIntegrationResponse deleteIntegration(
      CloudWatchLogsClient client, DeleteIntegrationRequest request) {
    return client.deleteIntegration(request);
  }

  public static DeleteLogGroupResponse deleteLogGroup(
      CloudWatchLogsClient client, DeleteLogGroupRequest request) {
    return client.deleteLogGroup(request);
  }

  public static DeleteLookupTableResponse deleteLookupTable(
      CloudWatchLogsClient client, DeleteLookupTableRequest request) {
    return client.deleteLookupTable(request);
  }

  public static DeleteResourcePolicyResponse deleteResourcePolicy(
      CloudWatchLogsClient client, DeleteResourcePolicyRequest request) {
    return client.deleteResourcePolicy(request);
  }

  public static DeleteRetentionPolicyResponse deleteRetentionPolicy(
      CloudWatchLogsClient client, DeleteRetentionPolicyRequest request) {
    return client.deleteRetentionPolicy(request);
  }

  public static DeleteTransformerResponse deleteTransformer(
      CloudWatchLogsClient client, DeleteTransformerRequest request) {
    return client.deleteTransformer(request);
  }

  public static DescribeAccountPoliciesResponse describeAccountPolicies(
      CloudWatchLogsClient client, DescribeAccountPoliciesRequest request) {
    return client.describeAccountPolicies(request);
  }

  public static List<ConfigurationTemplate> describeConfigurationTemplates(
      CloudWatchLogsClient client, DescribeConfigurationTemplatesRequest request) {
    return client.describeConfigurationTemplatesPaginator(request).stream()
        .flatMap(r -> r.configurationTemplates().stream())
        .collect(Collectors.toList());
  }

  public static DescribeExportTasksResponse describeExportTasks(
      CloudWatchLogsClient client, DescribeExportTasksRequest request) {
    return client.describeExportTasks(request);
  }

  public static DescribeImportTaskBatchesResponse describeImportTaskBatches(
      CloudWatchLogsClient client, DescribeImportTaskBatchesRequest request) {
    return client.describeImportTaskBatches(request);
  }

  public static DescribeImportTasksResponse describeImportTasks(
      CloudWatchLogsClient client, DescribeImportTasksRequest request) {
    return client.describeImportTasks(request);
  }

  public static DescribeIndexPoliciesResponse describeIndexPolicies(
      CloudWatchLogsClient client, DescribeIndexPoliciesRequest request) {
    return client.describeIndexPolicies(request);
  }

  public static List<LogGroup> describeLogGroups(
      CloudWatchLogsClient client, DescribeLogGroupsRequest request) {
    return client.describeLogGroupsPaginator(request).stream()
        .flatMap(r -> r.logGroups().stream())
        .collect(Collectors.toList());
  }

  public static DescribeLookupTablesResponse describeLookupTables(
      CloudWatchLogsClient client, DescribeLookupTablesRequest request) {
    return client.describeLookupTables(request);
  }

  public static DescribeResourcePoliciesResponse describeResourcePolicies(
      CloudWatchLogsClient client, DescribeResourcePoliciesRequest request) {
    return client.describeResourcePolicies(request);
  }

  public static DisassociateKmsKeyResponse disassociateKmsKey(
      CloudWatchLogsClient client, DisassociateKmsKeyRequest request) {
    return client.disassociateKmsKey(request);
  }

  public static DisassociateSourceFromS3TableIntegrationResponse
      disassociateSourceFromS3TableIntegration(
          CloudWatchLogsClient client, DisassociateSourceFromS3TableIntegrationRequest request) {
    return client.disassociateSourceFromS3TableIntegration(request);
  }

  public static GetDataProtectionPolicyResponse getDataProtectionPolicy(
      CloudWatchLogsClient client, GetDataProtectionPolicyRequest request) {
    return client.getDataProtectionPolicy(request);
  }

  public static GetIntegrationResponse getIntegration(
      CloudWatchLogsClient client, GetIntegrationRequest request) {
    return client.getIntegration(request);
  }

  public static GetLookupTableResponse getLookupTable(
      CloudWatchLogsClient client, GetLookupTableRequest request) {
    return client.getLookupTable(request);
  }

  public static GetTransformerResponse getTransformer(
      CloudWatchLogsClient client, GetTransformerRequest request) {
    return client.getTransformer(request);
  }

  public static List<AggregateLogGroupSummary> listAggregateLogGroupSummaries(
      CloudWatchLogsClient client, ListAggregateLogGroupSummariesRequest request) {
    return client.listAggregateLogGroupSummariesPaginator(request).stream()
        .flatMap(r -> r.aggregateLogGroupSummaries().stream())
        .collect(Collectors.toList());
  }

  public static ListIntegrationsResponse listIntegrations(
      CloudWatchLogsClient client, ListIntegrationsRequest request) {
    return client.listIntegrations(request);
  }

  public static ListLogGroupsResponse listLogGroups(
      CloudWatchLogsClient client, ListLogGroupsRequest request) {
    return client.listLogGroups(request);
  }

  public static List<String> listLogGroupsForQuery(
      CloudWatchLogsClient client, ListLogGroupsForQueryRequest request) {
    return client.listLogGroupsForQueryPaginator(request).stream()
        .flatMap(r -> r.logGroupIdentifiers().stream())
        .collect(Collectors.toList());
  }

  public static List<S3TableIntegrationSource> listSourcesForS3TableIntegration(
      CloudWatchLogsClient client, ListSourcesForS3TableIntegrationRequest request) {
    return client.listSourcesForS3TableIntegrationPaginator(request).stream()
        .flatMap(r -> r.sources().stream())
        .collect(Collectors.toList());
  }

  public static PutAccountPolicyResponse putAccountPolicy(
      CloudWatchLogsClient client, PutAccountPolicyRequest request) {
    return client.putAccountPolicy(request);
  }

  public static PutBearerTokenAuthenticationResponse putBearerTokenAuthentication(
      CloudWatchLogsClient client, PutBearerTokenAuthenticationRequest request) {
    return client.putBearerTokenAuthentication(request);
  }

  public static PutDataProtectionPolicyResponse putDataProtectionPolicy(
      CloudWatchLogsClient client, PutDataProtectionPolicyRequest request) {
    return client.putDataProtectionPolicy(request);
  }

  public static PutIndexPolicyResponse putIndexPolicy(
      CloudWatchLogsClient client, PutIndexPolicyRequest request) {
    return client.putIndexPolicy(request);
  }

  public static PutIntegrationResponse putIntegration(
      CloudWatchLogsClient client, PutIntegrationRequest request) {
    return client.putIntegration(request);
  }

  public static PutLogGroupDeletionProtectionResponse putLogGroupDeletionProtection(
      CloudWatchLogsClient client, PutLogGroupDeletionProtectionRequest request) {
    return client.putLogGroupDeletionProtection(request);
  }

  public static PutResourcePolicyResponse putResourcePolicy(
      CloudWatchLogsClient client, PutResourcePolicyRequest request) {
    return client.putResourcePolicy(request);
  }

  public static PutRetentionPolicyResponse putRetentionPolicy(
      CloudWatchLogsClient client, PutRetentionPolicyRequest request) {
    return client.putRetentionPolicy(request);
  }

  public static PutTransformerResponse putTransformer(
      CloudWatchLogsClient client, PutTransformerRequest request) {
    return client.putTransformer(request);
  }

  public static TestTransformerResponse testTransformer(
      CloudWatchLogsClient client, TestTransformerRequest request) {
    return client.testTransformer(request);
  }

  public static UpdateLookupTableResponse updateLookupTable(
      CloudWatchLogsClient client, UpdateLookupTableRequest request) {
    return client.updateLookupTable(request);
  }
}
