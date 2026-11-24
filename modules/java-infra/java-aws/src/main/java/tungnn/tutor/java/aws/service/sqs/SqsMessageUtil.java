package tungnn.tutor.java.aws.service.sqs;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

public final class SqsMessageUtil {
    private SqsMessageUtil() {}

    public static SendMessageResponse sendMessage(SqsClient client, SendMessageRequest request) {
        return client.sendMessage(request);
    }

    public static SendMessageBatchResponse sendMessageBatch(SqsClient client, SendMessageBatchRequest request) {
        return client.sendMessageBatch(request);
    }

    public static ReceiveMessageResponse receiveMessage(SqsClient client, ReceiveMessageRequest request) {
        return client.receiveMessage(request);
    }

    public static DeleteMessageResponse deleteMessage(SqsClient client, DeleteMessageRequest request) {
        return client.deleteMessage(request);
    }

    public static DeleteMessageBatchResponse deleteMessageBatch(SqsClient client, DeleteMessageBatchRequest request) {
        return client.deleteMessageBatch(request);
    }

    public static ChangeMessageVisibilityResponse changeMessageVisibility(
            SqsClient client, ChangeMessageVisibilityRequest request) {
        return client.changeMessageVisibility(request);
    }

    public static ChangeMessageVisibilityBatchResponse changeMessageVisibilityBatch(
            SqsClient client, ChangeMessageVisibilityBatchRequest request) {
        return client.changeMessageVisibilityBatch(request);
    }

    public static StartMessageMoveTaskResponse startMessageMoveTask(
            SqsClient client, StartMessageMoveTaskRequest request) {
        return client.startMessageMoveTask(request);
    }

    public static CancelMessageMoveTaskResponse cancelMessageMoveTask(
            SqsClient client, CancelMessageMoveTaskRequest request) {
        return client.cancelMessageMoveTask(request);
    }

    public static ListMessageMoveTasksResponse listMessageMoveTasks(
            SqsClient client, ListMessageMoveTasksRequest request) {
        return client.listMessageMoveTasks(request);
    }
}
