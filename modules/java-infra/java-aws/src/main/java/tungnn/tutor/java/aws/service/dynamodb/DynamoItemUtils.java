package tungnn.tutor.java.aws.service.dynamodb;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

public final class DynamoItemUtils {
    private DynamoItemUtils() {}

    public static GetItemResponse getItem(DynamoDbClient client, GetItemRequest request) {
        return client.getItem(request);
    }

    public static PutItemResponse putItem(DynamoDbClient client, PutItemRequest request) {
        return client.putItem(request);
    }

    public static UpdateItemResponse updateItem(DynamoDbClient client, UpdateItemRequest request) {
        return client.updateItem(request);
    }

    public static DeleteItemResponse deleteItem(DynamoDbClient client, DeleteItemRequest request) {
        return client.deleteItem(request);
    }

    public static List<Map<String, AttributeValue>> query(DynamoDbClient client, QueryRequest request) {
        return client.queryPaginator(request).stream()
                .flatMap(r -> r.items().stream())
                .collect(Collectors.toList());
    }

    public static List<Map<String, AttributeValue>> scan(DynamoDbClient client, ScanRequest request) {
        return client.scanPaginator(request).stream()
                .flatMap(r -> r.items().stream())
                .collect(Collectors.toList());
    }

    public static BatchGetItemResponse batchGetItem(DynamoDbClient client, BatchGetItemRequest request) {
        return client.batchGetItem(request);
    }

    public static BatchWriteItemResponse batchWriteItem(DynamoDbClient client, BatchWriteItemRequest request) {
        return client.batchWriteItem(request);
    }

    public static TransactGetItemsResponse transactGetItems(DynamoDbClient client, TransactGetItemsRequest request) {
        return client.transactGetItems(request);
    }

    public static TransactWriteItemsResponse transactWriteItems(
            DynamoDbClient client, TransactWriteItemsRequest request) {
        return client.transactWriteItems(request);
    }

    public static ExecuteStatementResponse executeStatement(DynamoDbClient client, ExecuteStatementRequest request) {
        return client.executeStatement(request);
    }

    public static BatchExecuteStatementResponse batchExecuteStatement(
            DynamoDbClient client, BatchExecuteStatementRequest request) {
        return client.batchExecuteStatement(request);
    }

    public static ExecuteTransactionResponse executeTransaction(
            DynamoDbClient client, ExecuteTransactionRequest request) {
        return client.executeTransaction(request);
    }
}
