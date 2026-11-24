package tungnn.tutor.java.aws.service.autoscaling;

import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import software.amazon.awssdk.services.autoscaling.model.*;

public final class AutoScalingLifecycleHookUtils {
    private AutoScalingLifecycleHookUtils() {}

    public static CompleteLifecycleActionResponse completeLifecycleAction(
            AutoScalingClient client, CompleteLifecycleActionRequest request) {
        return client.completeLifecycleAction(request);
    }

    public static DeleteLifecycleHookResponse deleteLifecycleHook(
            AutoScalingClient client, DeleteLifecycleHookRequest request) {
        return client.deleteLifecycleHook(request);
    }

    public static DescribeLifecycleHookTypesResponse describeLifecycleHookTypes(
            AutoScalingClient client, DescribeLifecycleHookTypesRequest request) {
        return client.describeLifecycleHookTypes(request);
    }

    public static DescribeLifecycleHooksResponse describeLifecycleHooks(
            AutoScalingClient client, DescribeLifecycleHooksRequest request) {
        return client.describeLifecycleHooks(request);
    }

    public static PutLifecycleHookResponse putLifecycleHook(AutoScalingClient client, PutLifecycleHookRequest request) {
        return client.putLifecycleHook(request);
    }

    public static RecordLifecycleActionHeartbeatResponse recordLifecycleActionHeartbeat(
            AutoScalingClient client, RecordLifecycleActionHeartbeatRequest request) {
        return client.recordLifecycleActionHeartbeat(request);
    }
}
