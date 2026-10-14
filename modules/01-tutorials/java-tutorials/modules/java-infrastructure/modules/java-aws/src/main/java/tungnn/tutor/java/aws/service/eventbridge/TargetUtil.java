package tungnn.tutor.java.aws.service.eventbridge;

import java.util.List;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.*;

public final class TargetUtil {

  private TargetUtil() {}

  public static PutTargetsResponse putTargets(EventBridgeClient client, PutTargetsRequest request) {
    return client.putTargets(request);
  }

  public static RemoveTargetsResponse removeTargets(
      EventBridgeClient client, RemoveTargetsRequest request) {
    return client.removeTargets(request);
  }

  public static List<Target> listTargetsByRule(
      EventBridgeClient client, ListTargetsByRuleRequest request) {
    return List.copyOf(client.listTargetsByRule(request).targets());
  }
}
