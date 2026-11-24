package tungnn.tutor.java.aws.service.sqs;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

public final class SqsQueueUtils {
    private SqsQueueUtils() {}

    public static CreateQueueResponse createQueue(SqsClient client, CreateQueueRequest request) {
        return client.createQueue(request);
    }

    public static DeleteQueueResponse deleteQueue(SqsClient client, DeleteQueueRequest request) {
        return client.deleteQueue(request);
    }

    public static GetQueueAttributesResponse getQueueAttributes(SqsClient client, GetQueueAttributesRequest request) {
        return client.getQueueAttributes(request);
    }

    public static GetQueueUrlResponse getQueueUrl(SqsClient client, GetQueueUrlRequest request) {
        return client.getQueueUrl(request);
    }

    public static List<String> listDeadLetterSourceQueues(SqsClient client, ListDeadLetterSourceQueuesRequest request) {
        return client.listDeadLetterSourceQueuesPaginator(request).stream()
                .flatMap(r -> r.queueUrls().stream())
                .collect(Collectors.toList());
    }

    public static List<String> listQueues(SqsClient client, ListQueuesRequest request) {
        return client.listQueuesPaginator(request).stream()
                .flatMap(r -> r.queueUrls().stream())
                .collect(Collectors.toList());
    }

    public static PurgeQueueResponse purgeQueue(SqsClient client, PurgeQueueRequest request) {
        return client.purgeQueue(request);
    }

    public static SetQueueAttributesResponse setQueueAttributes(SqsClient client, SetQueueAttributesRequest request) {
        return client.setQueueAttributes(request);
    }

    public static AddPermissionResponse addPermission(SqsClient client, AddPermissionRequest request) {
        return client.addPermission(request);
    }

    public static RemovePermissionResponse removePermission(SqsClient client, RemovePermissionRequest request) {
        return client.removePermission(request);
    }
}
