package tungnn.tutor.java.aws.service.route53;

import software.amazon.awssdk.services.route53.Route53Client;
import software.amazon.awssdk.services.route53.model.*;

public final class Route53TagUtil {
    private Route53TagUtil() {}

    public static ChangeTagsForResourceResponse changeTagsForResource(
            Route53Client client, ChangeTagsForResourceRequest request) {
        return client.changeTagsForResource(request);
    }

    public static ListTagsForResourceResponse listTagsForResource(
            Route53Client client, ListTagsForResourceRequest request) {
        return client.listTagsForResource(request);
    }

    public static ListTagsForResourcesResponse listTagsForResources(
            Route53Client client, ListTagsForResourcesRequest request) {
        return client.listTagsForResources(request);
    }
}
