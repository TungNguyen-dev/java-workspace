package tungnn.tutor.java.aws.service.ecr;

import software.amazon.awssdk.services.ecr.EcrClient;
import software.amazon.awssdk.services.ecr.model.*;

public final class EcrLifecyclePolicyUtil {

  private EcrLifecyclePolicyUtil() {}

  public static GetLifecyclePolicyResponse getLifecyclePolicy(
      EcrClient ecrClient, GetLifecyclePolicyRequest request) {
    return ecrClient.getLifecyclePolicy(request);
  }

  public static PutLifecyclePolicyResponse putLifecyclePolicy(
      EcrClient ecrClient, PutLifecyclePolicyRequest request) {
    return ecrClient.putLifecyclePolicy(request);
  }

  public static DeleteLifecyclePolicyResponse deleteLifecyclePolicy(
      EcrClient ecrClient, DeleteLifecyclePolicyRequest request) {
    return ecrClient.deleteLifecyclePolicy(request);
  }

  public static GetLifecyclePolicyPreviewResponse getLifecyclePolicyPreview(
      EcrClient ecrClient, GetLifecyclePolicyPreviewRequest request) {
    return ecrClient.getLifecyclePolicyPreview(request);
  }

  public static StartLifecyclePolicyPreviewResponse startLifecyclePolicyPreview(
      EcrClient ecrClient, StartLifecyclePolicyPreviewRequest request) {
    return ecrClient.startLifecyclePolicyPreview(request);
  }
}
