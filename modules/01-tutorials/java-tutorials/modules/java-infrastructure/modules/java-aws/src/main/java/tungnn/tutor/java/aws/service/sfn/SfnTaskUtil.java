package tungnn.tutor.java.aws.service.sfn;

import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.*;

public final class SfnTaskUtil {

  private SfnTaskUtil() {}

  public static SendTaskSuccessResponse sendTaskSuccess(
      SfnClient client, SendTaskSuccessRequest request) {
    return client.sendTaskSuccess(request);
  }

  public static SendTaskFailureResponse sendTaskFailure(
      SfnClient client, SendTaskFailureRequest request) {
    return client.sendTaskFailure(request);
  }

  public static SendTaskHeartbeatResponse sendTaskHeartbeat(
      SfnClient client, SendTaskHeartbeatRequest request) {
    return client.sendTaskHeartbeat(request);
  }
}
