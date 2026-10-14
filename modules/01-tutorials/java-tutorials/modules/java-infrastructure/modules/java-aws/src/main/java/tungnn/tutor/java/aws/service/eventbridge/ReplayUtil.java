package tungnn.tutor.java.aws.service.eventbridge;

import java.util.List;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.*;

public final class ReplayUtil {

  private ReplayUtil() {}

  public static StartReplayResponse startReplay(
      EventBridgeClient client, StartReplayRequest request) {
    return client.startReplay(request);
  }

  public static CancelReplayResponse cancelReplay(
      EventBridgeClient client, CancelReplayRequest request) {
    return client.cancelReplay(request);
  }

  public static DescribeReplayResponse describeReplay(
      EventBridgeClient client, DescribeReplayRequest request) {
    return client.describeReplay(request);
  }

  public static List<Replay> listReplays(EventBridgeClient client, ListReplaysRequest request) {
    return List.copyOf(client.listReplays(request).replays());
  }
}
