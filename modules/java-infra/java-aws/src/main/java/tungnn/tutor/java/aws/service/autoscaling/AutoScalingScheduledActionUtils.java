package tungnn.tutor.java.aws.service.autoscaling;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import software.amazon.awssdk.services.autoscaling.model.*;

public final class AutoScalingScheduledActionUtils {
    private AutoScalingScheduledActionUtils() {}

    public static BatchDeleteScheduledActionResponse batchDeleteScheduledAction(
            AutoScalingClient client, BatchDeleteScheduledActionRequest request) {
        return client.batchDeleteScheduledAction(request);
    }

    public static BatchPutScheduledUpdateGroupActionResponse batchPutScheduledUpdateGroupAction(
            AutoScalingClient client, BatchPutScheduledUpdateGroupActionRequest request) {
        return client.batchPutScheduledUpdateGroupAction(request);
    }

    public static DeleteScheduledActionResponse deleteScheduledAction(
            AutoScalingClient client, DeleteScheduledActionRequest request) {
        return client.deleteScheduledAction(request);
    }

    public static List<ScheduledUpdateGroupAction> describeScheduledActions(
            AutoScalingClient client, DescribeScheduledActionsRequest request) {
        return client.describeScheduledActionsPaginator(request).stream()
                .flatMap(r -> r.scheduledUpdateGroupActions().stream())
                .collect(Collectors.toList());
    }

    public static PutScheduledUpdateGroupActionResponse putScheduledUpdateGroupAction(
            AutoScalingClient client, PutScheduledUpdateGroupActionRequest request) {
        return client.putScheduledUpdateGroupAction(request);
    }
}
