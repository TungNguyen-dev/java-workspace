package tungnn.tutor.java.aws.service.eventbridge;

import java.util.List;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.*;

public final class RuleUtil {

  private RuleUtil() {}

  public static PutRuleResponse putRule(EventBridgeClient client, PutRuleRequest request) {
    return client.putRule(request);
  }

  public static DeleteRuleResponse deleteRule(EventBridgeClient client, DeleteRuleRequest request) {
    return client.deleteRule(request);
  }

  public static DescribeRuleResponse describeRule(
      EventBridgeClient client, DescribeRuleRequest request) {
    return client.describeRule(request);
  }

  public static EnableRuleResponse enableRule(EventBridgeClient client, EnableRuleRequest request) {
    return client.enableRule(request);
  }

  public static DisableRuleResponse disableRule(
      EventBridgeClient client, DisableRuleRequest request) {
    return client.disableRule(request);
  }

  public static List<Rule> listRules(EventBridgeClient client, ListRulesRequest request) {
    return List.copyOf(client.listRules(request).rules());
  }

  public static List<String> listRuleNamesByTarget(
      EventBridgeClient client, ListRuleNamesByTargetRequest request) {
    return List.copyOf(client.listRuleNamesByTarget(request).ruleNames());
  }
}
