package tungnn.tutor.java.aws.service.sfn;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.*;

public final class SfnActivityUtil {

  private SfnActivityUtil() {}

  public static CreateActivityResponse createActivity(
      SfnClient client, CreateActivityRequest request) {
    return client.createActivity(request);
  }

  public static DeleteActivityResponse deleteActivity(
      SfnClient client, DeleteActivityRequest request) {
    return client.deleteActivity(request);
  }

  public static DescribeActivityResponse describeActivity(
      SfnClient client, DescribeActivityRequest request) {
    return client.describeActivity(request);
  }

  public static GetActivityTaskResponse getActivityTask(
      SfnClient client, GetActivityTaskRequest request) {
    return client.getActivityTask(request);
  }

  public static List<ActivityListItem> listActivities(
      SfnClient client, ListActivitiesRequest request) {
    return client.listActivitiesPaginator(request).stream()
        .flatMap(page -> page.activities().stream())
        .collect(Collectors.toList());
  }
}
