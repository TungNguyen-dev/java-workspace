package tungnn.tutor.java.aws.service.eventbridge.scheduler;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.scheduler.SchedulerClient;
import software.amazon.awssdk.services.scheduler.model.*;

public final class ScheduleUtil {

  private ScheduleUtil() {}

  public static CreateScheduleResponse createSchedule(
      SchedulerClient client, CreateScheduleRequest request) {
    return client.createSchedule(request);
  }

  public static UpdateScheduleResponse updateSchedule(
      SchedulerClient client, UpdateScheduleRequest request) {
    return client.updateSchedule(request);
  }

  public static DeleteScheduleResponse deleteSchedule(
      SchedulerClient client, DeleteScheduleRequest request) {
    return client.deleteSchedule(request);
  }

  public static GetScheduleResponse getSchedule(
      SchedulerClient client, GetScheduleRequest request) {
    return client.getSchedule(request);
  }

  public static List<ScheduleSummary> listSchedules(
      SchedulerClient client, ListSchedulesRequest request) {
    return client.listSchedulesPaginator(request).stream()
        .flatMap(r -> r.schedules().stream())
        .collect(Collectors.toList());
  }
}
