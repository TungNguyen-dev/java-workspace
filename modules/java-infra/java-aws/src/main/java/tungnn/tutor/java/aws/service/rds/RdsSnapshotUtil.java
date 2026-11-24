package tungnn.tutor.java.aws.service.rds;

import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.*;

public final class RdsSnapshotUtil {
    private RdsSnapshotUtil() {}

    public static CancelExportTaskResponse cancelExportTask(RdsClient client, CancelExportTaskRequest request) {
        return client.cancelExportTask(request);
    }

    public static CopyDbClusterSnapshotResponse copyDBClusterSnapshot(
            RdsClient client, CopyDbClusterSnapshotRequest request) {
        return client.copyDBClusterSnapshot(request);
    }

    public static CopyDbSnapshotResponse copyDBSnapshot(RdsClient client, CopyDbSnapshotRequest request) {
        return client.copyDBSnapshot(request);
    }

    public static CreateDbClusterSnapshotResponse createDBClusterSnapshot(
            RdsClient client, CreateDbClusterSnapshotRequest request) {
        return client.createDBClusterSnapshot(request);
    }

    public static CreateDbSnapshotResponse createDBSnapshot(RdsClient client, CreateDbSnapshotRequest request) {
        return client.createDBSnapshot(request);
    }

    public static DeleteDbClusterAutomatedBackupResponse deleteDBClusterAutomatedBackup(
            RdsClient client, DeleteDbClusterAutomatedBackupRequest request) {
        return client.deleteDBClusterAutomatedBackup(request);
    }

    public static DeleteDbClusterSnapshotResponse deleteDBClusterSnapshot(
            RdsClient client, DeleteDbClusterSnapshotRequest request) {
        return client.deleteDBClusterSnapshot(request);
    }

    public static DeleteDbInstanceAutomatedBackupResponse deleteDBInstanceAutomatedBackup(
            RdsClient client, DeleteDbInstanceAutomatedBackupRequest request) {
        return client.deleteDBInstanceAutomatedBackup(request);
    }

    public static DeleteDbSnapshotResponse deleteDBSnapshot(RdsClient client, DeleteDbSnapshotRequest request) {
        return client.deleteDBSnapshot(request);
    }

    public static DescribeDbClusterAutomatedBackupsResponse describeDBClusterAutomatedBackups(
            RdsClient client, DescribeDbClusterAutomatedBackupsRequest request) {
        return client.describeDBClusterAutomatedBackups(request);
    }

    public static DescribeDbClusterSnapshotAttributesResponse describeDBClusterSnapshotAttributes(
            RdsClient client, DescribeDbClusterSnapshotAttributesRequest request) {
        return client.describeDBClusterSnapshotAttributes(request);
    }

    public static DescribeDbClusterSnapshotsResponse describeDBClusterSnapshots(
            RdsClient client, DescribeDbClusterSnapshotsRequest request) {
        return client.describeDBClusterSnapshots(request);
    }

    public static DescribeDbInstanceAutomatedBackupsResponse describeDBInstanceAutomatedBackups(
            RdsClient client, DescribeDbInstanceAutomatedBackupsRequest request) {
        return client.describeDBInstanceAutomatedBackups(request);
    }

    public static DescribeDbSnapshotAttributesResponse describeDBSnapshotAttributes(
            RdsClient client, DescribeDbSnapshotAttributesRequest request) {
        return client.describeDBSnapshotAttributes(request);
    }

    public static DescribeDbSnapshotTenantDatabasesResponse describeDBSnapshotTenantDatabases(
            RdsClient client, DescribeDbSnapshotTenantDatabasesRequest request) {
        return client.describeDBSnapshotTenantDatabases(request);
    }

    public static DescribeDbSnapshotsResponse describeDBSnapshots(
            RdsClient client, DescribeDbSnapshotsRequest request) {
        return client.describeDBSnapshots(request);
    }

    public static DescribeExportTasksResponse describeExportTasks(
            RdsClient client, DescribeExportTasksRequest request) {
        return client.describeExportTasks(request);
    }

    public static ModifyDbClusterSnapshotAttributeResponse modifyDBClusterSnapshotAttribute(
            RdsClient client, ModifyDbClusterSnapshotAttributeRequest request) {
        return client.modifyDBClusterSnapshotAttribute(request);
    }

    public static ModifyDbSnapshotResponse modifyDBSnapshot(RdsClient client, ModifyDbSnapshotRequest request) {
        return client.modifyDBSnapshot(request);
    }

    public static ModifyDbSnapshotAttributeResponse modifyDBSnapshotAttribute(
            RdsClient client, ModifyDbSnapshotAttributeRequest request) {
        return client.modifyDBSnapshotAttribute(request);
    }

    public static RestoreDbClusterFromSnapshotResponse restoreDBClusterFromSnapshot(
            RdsClient client, RestoreDbClusterFromSnapshotRequest request) {
        return client.restoreDBClusterFromSnapshot(request);
    }

    public static RestoreDbInstanceFromDbSnapshotResponse restoreDBInstanceFromDBSnapshot(
            RdsClient client, RestoreDbInstanceFromDbSnapshotRequest request) {
        return client.restoreDBInstanceFromDBSnapshot(request);
    }

    public static StartDbInstanceAutomatedBackupsReplicationResponse startDBInstanceAutomatedBackupsReplication(
            RdsClient client, StartDbInstanceAutomatedBackupsReplicationRequest request) {
        return client.startDBInstanceAutomatedBackupsReplication(request);
    }

    public static StartExportTaskResponse startExportTask(RdsClient client, StartExportTaskRequest request) {
        return client.startExportTask(request);
    }

    public static StopDbInstanceAutomatedBackupsReplicationResponse stopDBInstanceAutomatedBackupsReplication(
            RdsClient client, StopDbInstanceAutomatedBackupsReplicationRequest request) {
        return client.stopDBInstanceAutomatedBackupsReplication(request);
    }
}
