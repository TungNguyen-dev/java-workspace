package tungnn.tutor.java.aws.service.applicationautoscaling;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.applicationautoscaling.ApplicationAutoScalingClient;
import software.amazon.awssdk.services.applicationautoscaling.model.*;

public final class AppAutoScalingScheduledActionUtil {
  private AppAutoScalingScheduledActionUtil() {}

  public static DeleteScheduledActionResponse deleteScheduledAction(
      ApplicationAutoScalingClient client, DeleteScheduledActionRequest request) {
    return client.deleteScheduledAction(request);
  }

  public static List<ScheduledAction> describeScheduledActions(
      ApplicationAutoScalingClient client, DescribeScheduledActionsRequest request) {
    return client.describeScheduledActionsPaginator(request).stream()
        .flatMap(r -> r.scheduledActions().stream())
        .collect(Collectors.toList());
  }

  public static PutScheduledActionResponse putScheduledAction(
      ApplicationAutoScalingClient client, PutScheduledActionRequest request) {
    return client.putScheduledAction(request);
  }
}
