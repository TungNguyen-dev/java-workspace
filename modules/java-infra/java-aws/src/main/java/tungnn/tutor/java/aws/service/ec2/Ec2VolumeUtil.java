package tungnn.tutor.java.aws.service.ec2;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

public final class Ec2VolumeUtil {
    private Ec2VolumeUtil() {}

    public static AttachVolumeResponse attachVolume(Ec2Client client, AttachVolumeRequest request) {
        return client.attachVolume(request);
    }

    public static CopyVolumesResponse copyVolumes(Ec2Client client, CopyVolumesRequest request) {
        return client.copyVolumes(request);
    }

    public static CreateReplaceRootVolumeTaskResponse createReplaceRootVolumeTask(
            Ec2Client client, CreateReplaceRootVolumeTaskRequest request) {
        return client.createReplaceRootVolumeTask(request);
    }

    public static CreateVolumeResponse createVolume(Ec2Client client, CreateVolumeRequest request) {
        return client.createVolume(request);
    }

    public static DeleteVolumeResponse deleteVolume(Ec2Client client, DeleteVolumeRequest request) {
        return client.deleteVolume(request);
    }

    public static List<ReplaceRootVolumeTask> describeReplaceRootVolumeTasks(
            Ec2Client client, DescribeReplaceRootVolumeTasksRequest request) {
        return client.describeReplaceRootVolumeTasksPaginator(request).stream()
                .flatMap(r -> r.replaceRootVolumeTasks().stream())
                .collect(Collectors.toList());
    }

    public static DescribeVolumeAttributeResponse describeVolumeAttribute(
            Ec2Client client, DescribeVolumeAttributeRequest request) {
        return client.describeVolumeAttribute(request);
    }

    public static List<VolumeStatusItem> describeVolumeStatus(Ec2Client client, DescribeVolumeStatusRequest request) {
        return client.describeVolumeStatusPaginator(request).stream()
                .flatMap(r -> r.volumeStatuses().stream())
                .collect(Collectors.toList());
    }

    public static List<Volume> describeVolumes(Ec2Client client, DescribeVolumesRequest request) {
        return client.describeVolumesPaginator(request).stream()
                .flatMap(r -> r.volumes().stream())
                .collect(Collectors.toList());
    }

    public static List<VolumeModification> describeVolumesModifications(
            Ec2Client client, DescribeVolumesModificationsRequest request) {
        return client.describeVolumesModificationsPaginator(request).stream()
                .flatMap(r -> r.volumesModifications().stream())
                .collect(Collectors.toList());
    }

    public static DetachVolumeResponse detachVolume(Ec2Client client, DetachVolumeRequest request) {
        return client.detachVolume(request);
    }

    public static DisableEbsEncryptionByDefaultResponse disableEbsEncryptionByDefault(
            Ec2Client client, DisableEbsEncryptionByDefaultRequest request) {
        return client.disableEbsEncryptionByDefault(request);
    }

    public static EnableEbsEncryptionByDefaultResponse enableEbsEncryptionByDefault(
            Ec2Client client, EnableEbsEncryptionByDefaultRequest request) {
        return client.enableEbsEncryptionByDefault(request);
    }

    public static EnableVolumeIoResponse enableVolumeIO(Ec2Client client, EnableVolumeIoRequest request) {
        return client.enableVolumeIO(request);
    }

    public static GetEbsDefaultKmsKeyIdResponse getEbsDefaultKmsKeyId(
            Ec2Client client, GetEbsDefaultKmsKeyIdRequest request) {
        return client.getEbsDefaultKmsKeyId(request);
    }

    public static GetEbsEncryptionByDefaultResponse getEbsEncryptionByDefault(
            Ec2Client client, GetEbsEncryptionByDefaultRequest request) {
        return client.getEbsEncryptionByDefault(request);
    }

    public static ImportVolumeResponse importVolume(Ec2Client client, ImportVolumeRequest request) {
        return client.importVolume(request);
    }

    public static ListVolumesInRecycleBinResponse listVolumesInRecycleBin(
            Ec2Client client, ListVolumesInRecycleBinRequest request) {
        return client.listVolumesInRecycleBin(request);
    }

    public static ModifyEbsDefaultKmsKeyIdResponse modifyEbsDefaultKmsKeyId(
            Ec2Client client, ModifyEbsDefaultKmsKeyIdRequest request) {
        return client.modifyEbsDefaultKmsKeyId(request);
    }

    public static ModifyVolumeResponse modifyVolume(Ec2Client client, ModifyVolumeRequest request) {
        return client.modifyVolume(request);
    }

    public static ModifyVolumeAttributeResponse modifyVolumeAttribute(
            Ec2Client client, ModifyVolumeAttributeRequest request) {
        return client.modifyVolumeAttribute(request);
    }

    public static ResetEbsDefaultKmsKeyIdResponse resetEbsDefaultKmsKeyId(
            Ec2Client client, ResetEbsDefaultKmsKeyIdRequest request) {
        return client.resetEbsDefaultKmsKeyId(request);
    }

    public static RestoreVolumeFromRecycleBinResponse restoreVolumeFromRecycleBin(
            Ec2Client client, RestoreVolumeFromRecycleBinRequest request) {
        return client.restoreVolumeFromRecycleBin(request);
    }
}
