package tungnn.tutor.java.aws.service.elbv2;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.elasticloadbalancingv2.ElasticLoadBalancingV2Client;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.*;

public final class ElbRuleUtil {
  private ElbRuleUtil() {}

  public static CreateRuleResponse createRule(
      ElasticLoadBalancingV2Client client, CreateRuleRequest request) {
    return client.createRule(request);
  }

  public static DeleteRuleResponse deleteRule(
      ElasticLoadBalancingV2Client client, DeleteRuleRequest request) {
    return client.deleteRule(request);
  }

  public static List<Rule> describeRules(
      ElasticLoadBalancingV2Client client, DescribeRulesRequest request) {
    return client.describeRulesPaginator(request).stream()
        .flatMap(r -> r.rules().stream())
        .collect(Collectors.toList());
  }

  public static ModifyRuleResponse modifyRule(
      ElasticLoadBalancingV2Client client, ModifyRuleRequest request) {
    return client.modifyRule(request);
  }

  public static SetRulePrioritiesResponse setRulePriorities(
      ElasticLoadBalancingV2Client client, SetRulePrioritiesRequest request) {
    return client.setRulePriorities(request);
  }
}
