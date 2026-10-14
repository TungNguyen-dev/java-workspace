package tungnn.tutor.java.aws.service.eventbridge.scheduler;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.scheduler.SchedulerClient;
import software.amazon.awssdk.services.scheduler.model.*;

public final class ScheduleGroupUtil {

  private ScheduleGroupUtil() {}

  public static CreateScheduleGroupResponse createScheduleGroup(
      SchedulerClient client, CreateScheduleGroupRequest request) {
    return client.createScheduleGroup(request);
  }

  public static DeleteScheduleGroupResponse deleteScheduleGroup(
      SchedulerClient client, DeleteScheduleGroupRequest request) {
    return client.deleteScheduleGroup(request);
  }

  public static GetScheduleGroupResponse getScheduleGroup(
      SchedulerClient client, GetScheduleGroupRequest request) {
    return client.getScheduleGroup(request);
  }

  public static List<ScheduleGroupSummary> listScheduleGroups(
      SchedulerClient client, ListScheduleGroupsRequest request) {
    return client.listScheduleGroupsPaginator(request).stream()
        .flatMap(r -> r.scheduleGroups().stream())
        .collect(Collectors.toList());
  }
}
