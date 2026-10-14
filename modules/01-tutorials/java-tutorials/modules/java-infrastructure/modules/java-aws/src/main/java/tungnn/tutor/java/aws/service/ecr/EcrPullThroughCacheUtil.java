package tungnn.tutor.java.aws.service.ecr;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ecr.EcrClient;
import software.amazon.awssdk.services.ecr.model.*;

public final class EcrPullThroughCacheUtil {

  private EcrPullThroughCacheUtil() {}

  public static List<PullThroughCacheRule> describePullThroughCacheRules(
      EcrClient ecrClient, DescribePullThroughCacheRulesRequest request) {
    return ecrClient.describePullThroughCacheRulesPaginator(request).stream()
        .flatMap(r -> r.pullThroughCacheRules().stream())
        .collect(Collectors.toList());
  }

  public static CreatePullThroughCacheRuleResponse createPullThroughCacheRule(
      EcrClient ecrClient, CreatePullThroughCacheRuleRequest request) {
    return ecrClient.createPullThroughCacheRule(request);
  }

  public static UpdatePullThroughCacheRuleResponse updatePullThroughCacheRule(
      EcrClient ecrClient, UpdatePullThroughCacheRuleRequest request) {
    return ecrClient.updatePullThroughCacheRule(request);
  }

  public static DeletePullThroughCacheRuleResponse deletePullThroughCacheRule(
      EcrClient ecrClient, DeletePullThroughCacheRuleRequest request) {
    return ecrClient.deletePullThroughCacheRule(request);
  }

  public static ValidatePullThroughCacheRuleResponse validatePullThroughCacheRule(
      EcrClient ecrClient, ValidatePullThroughCacheRuleRequest request) {
    return ecrClient.validatePullThroughCacheRule(request);
  }
}
