package tungnn.tutor.java.aws.service.ecr;

import software.amazon.awssdk.services.ecr.EcrClient;
import software.amazon.awssdk.services.ecr.model.*;

public final class EcrLayerUtil {

  private EcrLayerUtil() {}

  public static BatchCheckLayerAvailabilityResponse batchCheckLayerAvailability(
      EcrClient ecrClient, BatchCheckLayerAvailabilityRequest request) {
    return ecrClient.batchCheckLayerAvailability(request);
  }

  public static GetDownloadUrlForLayerResponse getDownloadUrlForLayer(
      EcrClient ecrClient, GetDownloadUrlForLayerRequest request) {
    return ecrClient.getDownloadUrlForLayer(request);
  }

  public static InitiateLayerUploadResponse initiateLayerUpload(
      EcrClient ecrClient, InitiateLayerUploadRequest request) {
    return ecrClient.initiateLayerUpload(request);
  }

  public static UploadLayerPartResponse uploadLayerPart(
      EcrClient ecrClient, UploadLayerPartRequest request) {
    return ecrClient.uploadLayerPart(request);
  }

  public static CompleteLayerUploadResponse completeLayerUpload(
      EcrClient ecrClient, CompleteLayerUploadRequest request) {
    return ecrClient.completeLayerUpload(request);
  }
}
