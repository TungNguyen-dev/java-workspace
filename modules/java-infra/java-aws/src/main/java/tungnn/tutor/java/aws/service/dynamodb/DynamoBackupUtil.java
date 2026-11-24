package tungnn.tutor.java.aws.service.dynamodb;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

public final class DynamoBackupUtil {
    private DynamoBackupUtil() {}

    public static CreateBackupResponse createBackup(DynamoDbClient client, CreateBackupRequest request) {
        return client.createBackup(request);
    }

    public static DeleteBackupResponse deleteBackup(DynamoDbClient client, DeleteBackupRequest request) {
        return client.deleteBackup(request);
    }

    public static DescribeBackupResponse describeBackup(DynamoDbClient client, DescribeBackupRequest request) {
        return client.describeBackup(request);
    }

    public static ListBackupsResponse listBackups(DynamoDbClient client, ListBackupsRequest request) {
        return client.listBackups(request);
    }

    public static DescribeContinuousBackupsResponse describeContinuousBackups(
            DynamoDbClient client, DescribeContinuousBackupsRequest request) {
        return client.describeContinuousBackups(request);
    }

    public static UpdateContinuousBackupsResponse updateContinuousBackups(
            DynamoDbClient client, UpdateContinuousBackupsRequest request) {
        return client.updateContinuousBackups(request);
    }

    public static RestoreTableFromBackupResponse restoreTableFromBackup(
            DynamoDbClient client, RestoreTableFromBackupRequest request) {
        return client.restoreTableFromBackup(request);
    }

    public static RestoreTableToPointInTimeResponse restoreTableToPointInTime(
            DynamoDbClient client, RestoreTableToPointInTimeRequest request) {
        return client.restoreTableToPointInTime(request);
    }
}
