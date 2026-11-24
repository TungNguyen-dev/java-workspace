package tungnn.tutor.java.aws.service.autoscaling;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import software.amazon.awssdk.services.autoscaling.model.*;

public final class AutoScalingTagUtil {
    private AutoScalingTagUtil() {}

    public static CreateOrUpdateTagsResponse createOrUpdateTags(
            AutoScalingClient client, CreateOrUpdateTagsRequest request) {
        return client.createOrUpdateTags(request);
    }

    public static DeleteTagsResponse deleteTags(AutoScalingClient client, DeleteTagsRequest request) {
        return client.deleteTags(request);
    }

    public static List<TagDescription> describeTags(AutoScalingClient client, DescribeTagsRequest request) {
        return client.describeTagsPaginator(request).stream()
                .flatMap(r -> r.tags().stream())
                .collect(Collectors.toList());
    }
}
