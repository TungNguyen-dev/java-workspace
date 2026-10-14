package tungnn.tutor.java.aws.service.ecr;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ecr.EcrClient;
import software.amazon.awssdk.services.ecr.model.*;

public final class EcrImageUtil {

  private EcrImageUtil() {}

  public static List<ImageIdentifier> listImages(EcrClient ecrClient, ListImagesRequest request) {
    return ecrClient.listImagesPaginator(request).stream()
        .flatMap(r -> r.imageIds().stream())
        .collect(Collectors.toList());
  }

  public static List<ImageDetail> describeImages(
      EcrClient ecrClient, DescribeImagesRequest request) {
    return ecrClient.describeImagesPaginator(request).stream()
        .flatMap(r -> r.imageDetails().stream())
        .collect(Collectors.toList());
  }

  public static List<ImageScanFinding> describeImageScanFindings(
      EcrClient ecrClient, DescribeImageScanFindingsRequest request) {
    return ecrClient.describeImageScanFindingsPaginator(request).stream()
        .flatMap(r -> r.imageScanFindings().findings().stream())
        .collect(Collectors.toList());
  }

  public static BatchDeleteImageResponse batchDeleteImage(
      EcrClient ecrClient, BatchDeleteImageRequest request) {
    return ecrClient.batchDeleteImage(request);
  }

  public static BatchGetImageResponse batchGetImage(
      EcrClient ecrClient, BatchGetImageRequest request) {
    return ecrClient.batchGetImage(request);
  }

  public static PutImageResponse putImage(EcrClient ecrClient, PutImageRequest request) {
    return ecrClient.putImage(request);
  }

  public static PutImageScanningConfigurationResponse putImageScanningConfiguration(
      EcrClient ecrClient, PutImageScanningConfigurationRequest request) {
    return ecrClient.putImageScanningConfiguration(request);
  }

  public static PutImageTagMutabilityResponse putImageTagMutability(
      EcrClient ecrClient, PutImageTagMutabilityRequest request) {
    return ecrClient.putImageTagMutability(request);
  }

  public static StartImageScanResponse startImageScan(
      EcrClient ecrClient, StartImageScanRequest request) {
    return ecrClient.startImageScan(request);
  }

  public static DescribeImageReplicationStatusResponse describeImageReplicationStatus(
      EcrClient ecrClient, DescribeImageReplicationStatusRequest request) {
    return ecrClient.describeImageReplicationStatus(request);
  }
}
