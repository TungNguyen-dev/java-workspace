package tungnn.tutor.java.aws.service.sfn;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.*;

public final class SfnMapRunUtil {

  private SfnMapRunUtil() {}

  public static DescribeMapRunResponse describeMapRun(
      SfnClient client, DescribeMapRunRequest request) {
    return client.describeMapRun(request);
  }

  public static UpdateMapRunResponse updateMapRun(SfnClient client, UpdateMapRunRequest request) {
    return client.updateMapRun(request);
  }

  public static List<MapRunListItem> listMapRuns(SfnClient client, ListMapRunsRequest request) {
    return client.listMapRunsPaginator(request).stream()
        .flatMap(page -> page.mapRuns().stream())
        .collect(Collectors.toList());
  }
}
