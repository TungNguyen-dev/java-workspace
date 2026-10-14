package tungnn.tutor.java.aws.service.ssm;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;

public final class SsmSessionUtil {

  private SsmSessionUtil() {}

  public static List<Session> describeSessions(SsmClient client, DescribeSessionsRequest request) {
    return client.describeSessionsPaginator(request).stream()
        .flatMap(r -> r.sessions().stream())
        .collect(Collectors.toList());
  }

  public static GetConnectionStatusResponse getConnectionStatus(
      SsmClient client, GetConnectionStatusRequest request) {
    return client.getConnectionStatus(request);
  }

  public static ResumeSessionResponse resumeSession(
      SsmClient client, ResumeSessionRequest request) {
    return client.resumeSession(request);
  }

  public static StartSessionResponse startSession(SsmClient client, StartSessionRequest request) {
    return client.startSession(request);
  }

  public static TerminateSessionResponse terminateSession(
      SsmClient client, TerminateSessionRequest request) {
    return client.terminateSession(request);
  }
}
