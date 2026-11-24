package tungnn.tutor.java.aws.service.rds;

import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.*;

public final class RdsTagUtils {
    private RdsTagUtils() {}

    public static AddTagsToResourceResponse addTagsToResource(RdsClient client, AddTagsToResourceRequest request) {
        return client.addTagsToResource(request);
    }

    public static ListTagsForResourceResponse listTagsForResource(
            RdsClient client, ListTagsForResourceRequest request) {
        return client.listTagsForResource(request);
    }

    public static RemoveTagsFromResourceResponse removeTagsFromResource(
            RdsClient client, RemoveTagsFromResourceRequest request) {
        return client.removeTagsFromResource(request);
    }
}
