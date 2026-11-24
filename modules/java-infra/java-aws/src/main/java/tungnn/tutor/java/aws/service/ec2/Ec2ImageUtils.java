package tungnn.tutor.java.aws.service.ec2;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

public final class Ec2ImageUtils {
    private Ec2ImageUtils() {}

    public static AttachImageWatermarkResponse attachImageWatermark(
            Ec2Client client, AttachImageWatermarkRequest request) {
        return client.attachImageWatermark(request);
    }

    public static CancelImageLaunchPermissionResponse cancelImageLaunchPermission(
            Ec2Client client, CancelImageLaunchPermissionRequest request) {
        return client.cancelImageLaunchPermission(request);
    }

    public static CopyFpgaImageResponse copyFpgaImage(Ec2Client client, CopyFpgaImageRequest request) {
        return client.copyFpgaImage(request);
    }

    public static CopyImageResponse copyImage(Ec2Client client, CopyImageRequest request) {
        return client.copyImage(request);
    }

    public static CreateFpgaImageResponse createFpgaImage(Ec2Client client, CreateFpgaImageRequest request) {
        return client.createFpgaImage(request);
    }

    public static CreateImageResponse createImage(Ec2Client client, CreateImageRequest request) {
        return client.createImage(request);
    }

    public static CreateImageUsageReportResponse createImageUsageReport(
            Ec2Client client, CreateImageUsageReportRequest request) {
        return client.createImageUsageReport(request);
    }

    public static CreateRestoreImageTaskResponse createRestoreImageTask(
            Ec2Client client, CreateRestoreImageTaskRequest request) {
        return client.createRestoreImageTask(request);
    }

    public static CreateStoreImageTaskResponse createStoreImageTask(
            Ec2Client client, CreateStoreImageTaskRequest request) {
        return client.createStoreImageTask(request);
    }

    public static DeleteFpgaImageResponse deleteFpgaImage(Ec2Client client, DeleteFpgaImageRequest request) {
        return client.deleteFpgaImage(request);
    }

    public static DeleteImageUsageReportResponse deleteImageUsageReport(
            Ec2Client client, DeleteImageUsageReportRequest request) {
        return client.deleteImageUsageReport(request);
    }

    public static DeregisterImageResponse deregisterImage(Ec2Client client, DeregisterImageRequest request) {
        return client.deregisterImage(request);
    }

    public static List<ExportImageTask> describeExportImageTasks(
            Ec2Client client, DescribeExportImageTasksRequest request) {
        return client.describeExportImageTasksPaginator(request).stream()
                .flatMap(r -> r.exportImageTasks().stream())
                .collect(Collectors.toList());
    }

    public static List<DescribeFastLaunchImagesSuccessItem> describeFastLaunchImages(
            Ec2Client client, DescribeFastLaunchImagesRequest request) {
        return client.describeFastLaunchImagesPaginator(request).stream()
                .flatMap(r -> r.fastLaunchImages().stream())
                .collect(Collectors.toList());
    }

    public static DescribeFpgaImageAttributeResponse describeFpgaImageAttribute(
            Ec2Client client, DescribeFpgaImageAttributeRequest request) {
        return client.describeFpgaImageAttribute(request);
    }

    public static List<FpgaImage> describeFpgaImages(Ec2Client client, DescribeFpgaImagesRequest request) {
        return client.describeFpgaImagesPaginator(request).stream()
                .flatMap(r -> r.fpgaImages().stream())
                .collect(Collectors.toList());
    }

    public static DescribeImageAttributeResponse describeImageAttribute(
            Ec2Client client, DescribeImageAttributeRequest request) {
        return client.describeImageAttribute(request);
    }

    public static List<ImageReference> describeImageReferences(
            Ec2Client client, DescribeImageReferencesRequest request) {
        return client.describeImageReferencesPaginator(request).stream()
                .flatMap(r -> r.imageReferences().stream())
                .collect(Collectors.toList());
    }

    public static List<ImageUsageReportEntry> describeImageUsageReportEntries(
            Ec2Client client, DescribeImageUsageReportEntriesRequest request) {
        return client.describeImageUsageReportEntriesPaginator(request).stream()
                .flatMap(r -> r.imageUsageReportEntries().stream())
                .collect(Collectors.toList());
    }

    public static List<ImageUsageReport> describeImageUsageReports(
            Ec2Client client, DescribeImageUsageReportsRequest request) {
        return client.describeImageUsageReportsPaginator(request).stream()
                .flatMap(r -> r.imageUsageReports().stream())
                .collect(Collectors.toList());
    }

    public static List<Image> describeImages(Ec2Client client, DescribeImagesRequest request) {
        return client.describeImagesPaginator(request).stream()
                .flatMap(r -> r.images().stream())
                .collect(Collectors.toList());
    }

    public static List<ImportImageTask> describeImportImageTasks(
            Ec2Client client, DescribeImportImageTasksRequest request) {
        return client.describeImportImageTasksPaginator(request).stream()
                .flatMap(r -> r.importImageTasks().stream())
                .collect(Collectors.toList());
    }

    public static List<InstanceImageMetadata> describeInstanceImageMetadata(
            Ec2Client client, DescribeInstanceImageMetadataRequest request) {
        return client.describeInstanceImageMetadataPaginator(request).stream()
                .flatMap(r -> r.instanceImageMetadata().stream())
                .collect(Collectors.toList());
    }

    public static List<StoreImageTaskResult> describeStoreImageTasks(
            Ec2Client client, DescribeStoreImageTasksRequest request) {
        return client.describeStoreImageTasksPaginator(request).stream()
                .flatMap(r -> r.storeImageTaskResults().stream())
                .collect(Collectors.toList());
    }

    public static DetachImageWatermarkResponse detachImageWatermark(
            Ec2Client client, DetachImageWatermarkRequest request) {
        return client.detachImageWatermark(request);
    }

    public static DisableAllowedImagesSettingsResponse disableAllowedImagesSettings(
            Ec2Client client, DisableAllowedImagesSettingsRequest request) {
        return client.disableAllowedImagesSettings(request);
    }

    public static DisableFastLaunchResponse disableFastLaunch(Ec2Client client, DisableFastLaunchRequest request) {
        return client.disableFastLaunch(request);
    }

    public static DisableImageResponse disableImage(Ec2Client client, DisableImageRequest request) {
        return client.disableImage(request);
    }

    public static DisableImageBlockPublicAccessResponse disableImageBlockPublicAccess(
            Ec2Client client, DisableImageBlockPublicAccessRequest request) {
        return client.disableImageBlockPublicAccess(request);
    }

    public static DisableImageDeprecationResponse disableImageDeprecation(
            Ec2Client client, DisableImageDeprecationRequest request) {
        return client.disableImageDeprecation(request);
    }

    public static DisableImageDeregistrationProtectionResponse disableImageDeregistrationProtection(
            Ec2Client client, DisableImageDeregistrationProtectionRequest request) {
        return client.disableImageDeregistrationProtection(request);
    }

    public static EnableAllowedImagesSettingsResponse enableAllowedImagesSettings(
            Ec2Client client, EnableAllowedImagesSettingsRequest request) {
        return client.enableAllowedImagesSettings(request);
    }

    public static EnableFastLaunchResponse enableFastLaunch(Ec2Client client, EnableFastLaunchRequest request) {
        return client.enableFastLaunch(request);
    }

    public static EnableImageResponse enableImage(Ec2Client client, EnableImageRequest request) {
        return client.enableImage(request);
    }

    public static EnableImageBlockPublicAccessResponse enableImageBlockPublicAccess(
            Ec2Client client, EnableImageBlockPublicAccessRequest request) {
        return client.enableImageBlockPublicAccess(request);
    }

    public static EnableImageDeprecationResponse enableImageDeprecation(
            Ec2Client client, EnableImageDeprecationRequest request) {
        return client.enableImageDeprecation(request);
    }

    public static EnableImageDeregistrationProtectionResponse enableImageDeregistrationProtection(
            Ec2Client client, EnableImageDeregistrationProtectionRequest request) {
        return client.enableImageDeregistrationProtection(request);
    }

    public static ExportImageResponse exportImage(Ec2Client client, ExportImageRequest request) {
        return client.exportImage(request);
    }

    public static GetAllowedImagesSettingsResponse getAllowedImagesSettings(
            Ec2Client client, GetAllowedImagesSettingsRequest request) {
        return client.getAllowedImagesSettings(request);
    }

    public static GetImageAncestryResponse getImageAncestry(Ec2Client client, GetImageAncestryRequest request) {
        return client.getImageAncestry(request);
    }

    public static GetImageBlockPublicAccessStateResponse getImageBlockPublicAccessState(
            Ec2Client client, GetImageBlockPublicAccessStateRequest request) {
        return client.getImageBlockPublicAccessState(request);
    }

    public static ImportImageResponse importImage(Ec2Client client, ImportImageRequest request) {
        return client.importImage(request);
    }

    public static List<ImageRecycleBinInfo> listImagesInRecycleBin(
            Ec2Client client, ListImagesInRecycleBinRequest request) {
        return client.listImagesInRecycleBinPaginator(request).stream()
                .flatMap(r -> r.images().stream())
                .collect(Collectors.toList());
    }

    public static ModifyFpgaImageAttributeResponse modifyFpgaImageAttribute(
            Ec2Client client, ModifyFpgaImageAttributeRequest request) {
        return client.modifyFpgaImageAttribute(request);
    }

    public static ModifyImageAttributeResponse modifyImageAttribute(
            Ec2Client client, ModifyImageAttributeRequest request) {
        return client.modifyImageAttribute(request);
    }

    public static RegisterImageResponse registerImage(Ec2Client client, RegisterImageRequest request) {
        return client.registerImage(request);
    }

    public static ReplaceImageCriteriaInAllowedImagesSettingsResponse replaceImageCriteriaInAllowedImagesSettings(
            Ec2Client client, ReplaceImageCriteriaInAllowedImagesSettingsRequest request) {
        return client.replaceImageCriteriaInAllowedImagesSettings(request);
    }

    public static ResetFpgaImageAttributeResponse resetFpgaImageAttribute(
            Ec2Client client, ResetFpgaImageAttributeRequest request) {
        return client.resetFpgaImageAttribute(request);
    }

    public static ResetImageAttributeResponse resetImageAttribute(
            Ec2Client client, ResetImageAttributeRequest request) {
        return client.resetImageAttribute(request);
    }

    public static RestoreImageFromRecycleBinResponse restoreImageFromRecycleBin(
            Ec2Client client, RestoreImageFromRecycleBinRequest request) {
        return client.restoreImageFromRecycleBin(request);
    }
}
