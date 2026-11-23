package tungnn.tutor.java.aws.service.cloudwatch;

import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;

public final class CloudWatchInsightRuleUtil {
  private CloudWatchInsightRuleUtil() {}

  public static DeleteInsightRulesResponse deleteInsightRules(
      CloudWatchClient client, DeleteInsightRulesRequest request) {
    return client.deleteInsightRules(request);
  }

  public static DescribeInsightRulesResponse describeInsightRules(
      CloudWatchClient client, DescribeInsightRulesRequest request) {
    return client.describeInsightRules(request);
  }

  public static DisableInsightRulesResponse disableInsightRules(
      CloudWatchClient client, DisableInsightRulesRequest request) {
    return client.disableInsightRules(request);
  }

  public static EnableInsightRulesResponse enableInsightRules(
      CloudWatchClient client, EnableInsightRulesRequest request) {
    return client.enableInsightRules(request);
  }

  public static GetInsightRuleReportResponse getInsightRuleReport(
      CloudWatchClient client, GetInsightRuleReportRequest request) {
    return client.getInsightRuleReport(request);
  }

  public static ListManagedInsightRulesResponse listManagedInsightRules(
      CloudWatchClient client, ListManagedInsightRulesRequest request) {
    return client.listManagedInsightRules(request);
  }

  public static PutInsightRuleResponse putInsightRule(
      CloudWatchClient client, PutInsightRuleRequest request) {
    return client.putInsightRule(request);
  }

  public static PutManagedInsightRulesResponse putManagedInsightRules(
      CloudWatchClient client, PutManagedInsightRulesRequest request) {
    return client.putManagedInsightRules(request);
  }
}
