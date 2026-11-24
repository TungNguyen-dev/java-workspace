package tungnn.tutor.java.aws.service.cloudwatch;

import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;

public final class CloudWatchDashboardUtil {
    private CloudWatchDashboardUtil() {}

    public static DeleteDashboardsResponse deleteDashboards(CloudWatchClient client, DeleteDashboardsRequest request) {
        return client.deleteDashboards(request);
    }

    public static GetDashboardResponse getDashboard(CloudWatchClient client, GetDashboardRequest request) {
        return client.getDashboard(request);
    }

    public static ListDashboardsResponse listDashboards(CloudWatchClient client, ListDashboardsRequest request) {
        return client.listDashboards(request);
    }

    public static PutDashboardResponse putDashboard(CloudWatchClient client, PutDashboardRequest request) {
        return client.putDashboard(request);
    }
}
