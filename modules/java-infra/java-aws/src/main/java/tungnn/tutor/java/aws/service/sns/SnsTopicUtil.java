package tungnn.tutor.java.aws.service.sns;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

public final class SnsTopicUtil {
    private SnsTopicUtil() {}

    public static CreateTopicResponse createTopic(SnsClient client, CreateTopicRequest request) {
        return client.createTopic(request);
    }

    public static DeleteTopicResponse deleteTopic(SnsClient client, DeleteTopicRequest request) {
        return client.deleteTopic(request);
    }

    public static GetTopicAttributesResponse getTopicAttributes(SnsClient client, GetTopicAttributesRequest request) {
        return client.getTopicAttributes(request);
    }

    public static SetTopicAttributesResponse setTopicAttributes(SnsClient client, SetTopicAttributesRequest request) {
        return client.setTopicAttributes(request);
    }

    public static List<Topic> listTopics(SnsClient client, ListTopicsRequest request) {
        return client.listTopicsPaginator(request).stream()
                .flatMap(r -> r.topics().stream())
                .collect(Collectors.toList());
    }

    public static AddPermissionResponse addPermission(SnsClient client, AddPermissionRequest request) {
        return client.addPermission(request);
    }

    public static RemovePermissionResponse removePermission(SnsClient client, RemovePermissionRequest request) {
        return client.removePermission(request);
    }

    public static GetDataProtectionPolicyResponse getDataProtectionPolicy(
            SnsClient client, GetDataProtectionPolicyRequest request) {
        return client.getDataProtectionPolicy(request);
    }

    public static PutDataProtectionPolicyResponse putDataProtectionPolicy(
            SnsClient client, PutDataProtectionPolicyRequest request) {
        return client.putDataProtectionPolicy(request);
    }
}
