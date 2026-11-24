package tungnn.tutor.java.aws.service.cloudwatchlogs;

import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.*;

public final class CloudWatchLogsTagUtil {
    private CloudWatchLogsTagUtil() {}

    public static ListTagsForResourceResponse listTagsForResource(
            CloudWatchLogsClient client, ListTagsForResourceRequest request) {
        return client.listTagsForResource(request);
    }

    public static ListTagsLogGroupResponse listTagsLogGroup(
            CloudWatchLogsClient client, ListTagsLogGroupRequest request) {
        return client.listTagsLogGroup(request);
    }

    public static TagLogGroupResponse tagLogGroup(CloudWatchLogsClient client, TagLogGroupRequest request) {
        return client.tagLogGroup(request);
    }

    public static TagResourceResponse tagResource(CloudWatchLogsClient client, TagResourceRequest request) {
        return client.tagResource(request);
    }

    public static UntagLogGroupResponse untagLogGroup(CloudWatchLogsClient client, UntagLogGroupRequest request) {
        return client.untagLogGroup(request);
    }

    public static UntagResourceResponse untagResource(CloudWatchLogsClient client, UntagResourceRequest request) {
        return client.untagResource(request);
    }
}
