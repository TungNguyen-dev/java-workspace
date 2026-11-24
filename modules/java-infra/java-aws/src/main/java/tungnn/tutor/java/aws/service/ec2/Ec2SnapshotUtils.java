package tungnn.tutor.java.aws.service.ec2;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

public final class Ec2SnapshotUtils {
    private Ec2SnapshotUtils() {}

    public static CopySnapshotResponse copySnapshot(Ec2Client client, CopySnapshotRequest request) {
        return client.copySnapshot(request);
    }

    public static CreateSnapshotResponse createSnapshot(Ec2Client client, CreateSnapshotRequest request) {
        return client.createSnapshot(request);
    }

    public static CreateSnapshotsResponse createSnapshots(Ec2Client client, CreateSnapshotsRequest request) {
        return client.createSnapshots(request);
    }

    public static DeleteSnapshotResponse deleteSnapshot(Ec2Client client, DeleteSnapshotRequest request) {
        return client.deleteSnapshot(request);
    }

    public static List<DescribeFastSnapshotRestoreSuccessItem> describeFastSnapshotRestores(
            Ec2Client client, DescribeFastSnapshotRestoresRequest request) {
        return client.describeFastSnapshotRestoresPaginator(request).stream()
                .flatMap(r -> r.fastSnapshotRestores().stream())
                .collect(Collectors.toList());
    }

    public static List<ImportSnapshotTask> describeImportSnapshotTasks(
            Ec2Client client, DescribeImportSnapshotTasksRequest request) {
        return client.describeImportSnapshotTasksPaginator(request).stream()
                .flatMap(r -> r.importSnapshotTasks().stream())
                .collect(Collectors.toList());
    }

    public static DescribeLockedSnapshotsResponse describeLockedSnapshots(
            Ec2Client client, DescribeLockedSnapshotsRequest request) {
        return client.describeLockedSnapshots(request);
    }

    public static DescribeSnapshotAttributeResponse describeSnapshotAttribute(
            Ec2Client client, DescribeSnapshotAttributeRequest request) {
        return client.describeSnapshotAttribute(request);
    }

    public static List<SnapshotTierStatus> describeSnapshotTierStatus(
            Ec2Client client, DescribeSnapshotTierStatusRequest request) {
        return client.describeSnapshotTierStatusPaginator(request).stream()
                .flatMap(r -> r.snapshotTierStatuses().stream())
                .collect(Collectors.toList());
    }

    public static List<Snapshot> describeSnapshots(Ec2Client client, DescribeSnapshotsRequest request) {
        return client.describeSnapshotsPaginator(request).stream()
                .flatMap(r -> r.snapshots().stream())
                .collect(Collectors.toList());
    }

    public static DisableFastSnapshotRestoresResponse disableFastSnapshotRestores(
            Ec2Client client, DisableFastSnapshotRestoresRequest request) {
        return client.disableFastSnapshotRestores(request);
    }

    public static DisableSnapshotBlockPublicAccessResponse disableSnapshotBlockPublicAccess(
            Ec2Client client, DisableSnapshotBlockPublicAccessRequest request) {
        return client.disableSnapshotBlockPublicAccess(request);
    }

    public static EnableFastSnapshotRestoresResponse enableFastSnapshotRestores(
            Ec2Client client, EnableFastSnapshotRestoresRequest request) {
        return client.enableFastSnapshotRestores(request);
    }

    public static EnableSnapshotBlockPublicAccessResponse enableSnapshotBlockPublicAccess(
            Ec2Client client, EnableSnapshotBlockPublicAccessRequest request) {
        return client.enableSnapshotBlockPublicAccess(request);
    }

    public static GetSnapshotBlockPublicAccessStateResponse getSnapshotBlockPublicAccessState(
            Ec2Client client, GetSnapshotBlockPublicAccessStateRequest request) {
        return client.getSnapshotBlockPublicAccessState(request);
    }

    public static ImportSnapshotResponse importSnapshot(Ec2Client client, ImportSnapshotRequest request) {
        return client.importSnapshot(request);
    }

    public static List<SnapshotRecycleBinInfo> listSnapshotsInRecycleBin(
            Ec2Client client, ListSnapshotsInRecycleBinRequest request) {
        return client.listSnapshotsInRecycleBinPaginator(request).stream()
                .flatMap(r -> r.snapshots().stream())
                .collect(Collectors.toList());
    }

    public static LockSnapshotResponse lockSnapshot(Ec2Client client, LockSnapshotRequest request) {
        return client.lockSnapshot(request);
    }

    public static ModifySnapshotAttributeResponse modifySnapshotAttribute(
            Ec2Client client, ModifySnapshotAttributeRequest request) {
        return client.modifySnapshotAttribute(request);
    }

    public static ModifySnapshotTierResponse modifySnapshotTier(Ec2Client client, ModifySnapshotTierRequest request) {
        return client.modifySnapshotTier(request);
    }

    public static ResetSnapshotAttributeResponse resetSnapshotAttribute(
            Ec2Client client, ResetSnapshotAttributeRequest request) {
        return client.resetSnapshotAttribute(request);
    }

    public static RestoreSnapshotFromRecycleBinResponse restoreSnapshotFromRecycleBin(
            Ec2Client client, RestoreSnapshotFromRecycleBinRequest request) {
        return client.restoreSnapshotFromRecycleBin(request);
    }

    public static RestoreSnapshotTierResponse restoreSnapshotTier(
            Ec2Client client, RestoreSnapshotTierRequest request) {
        return client.restoreSnapshotTier(request);
    }

    public static UnlockSnapshotResponse unlockSnapshot(Ec2Client client, UnlockSnapshotRequest request) {
        return client.unlockSnapshot(request);
    }
}
