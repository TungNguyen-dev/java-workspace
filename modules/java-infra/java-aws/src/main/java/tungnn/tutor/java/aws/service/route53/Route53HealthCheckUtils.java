package tungnn.tutor.java.aws.service.route53;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.route53.Route53Client;
import software.amazon.awssdk.services.route53.model.*;

public final class Route53HealthCheckUtils {
    private Route53HealthCheckUtils() {}

    public static CreateHealthCheckResponse createHealthCheck(Route53Client client, CreateHealthCheckRequest request) {
        return client.createHealthCheck(request);
    }

    public static DeleteHealthCheckResponse deleteHealthCheck(Route53Client client, DeleteHealthCheckRequest request) {
        return client.deleteHealthCheck(request);
    }

    public static GetCheckerIpRangesResponse getCheckerIpRanges(
            Route53Client client, GetCheckerIpRangesRequest request) {
        return client.getCheckerIpRanges(request);
    }

    public static GetHealthCheckResponse getHealthCheck(Route53Client client, GetHealthCheckRequest request) {
        return client.getHealthCheck(request);
    }

    public static GetHealthCheckCountResponse getHealthCheckCount(
            Route53Client client, GetHealthCheckCountRequest request) {
        return client.getHealthCheckCount(request);
    }

    public static GetHealthCheckLastFailureReasonResponse getHealthCheckLastFailureReason(
            Route53Client client, GetHealthCheckLastFailureReasonRequest request) {
        return client.getHealthCheckLastFailureReason(request);
    }

    public static GetHealthCheckStatusResponse getHealthCheckStatus(
            Route53Client client, GetHealthCheckStatusRequest request) {
        return client.getHealthCheckStatus(request);
    }

    public static List<HealthCheck> listHealthChecks(Route53Client client, ListHealthChecksRequest request) {
        return client.listHealthChecksPaginator(request).stream()
                .flatMap(r -> r.healthChecks().stream())
                .collect(Collectors.toList());
    }

    public static UpdateHealthCheckResponse updateHealthCheck(Route53Client client, UpdateHealthCheckRequest request) {
        return client.updateHealthCheck(request);
    }
}
