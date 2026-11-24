package tungnn.tutor.java.aws.service.cloudwatchlogs;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.*;

public final class CloudWatchLogsSubscriptionUtil {
    private CloudWatchLogsSubscriptionUtil() {}

    public static CreateDeliveryResponse createDelivery(CloudWatchLogsClient client, CreateDeliveryRequest request) {
        return client.createDelivery(request);
    }

    public static DeleteDeliveryResponse deleteDelivery(CloudWatchLogsClient client, DeleteDeliveryRequest request) {
        return client.deleteDelivery(request);
    }

    public static DeleteDeliveryDestinationResponse deleteDeliveryDestination(
            CloudWatchLogsClient client, DeleteDeliveryDestinationRequest request) {
        return client.deleteDeliveryDestination(request);
    }

    public static DeleteDeliveryDestinationPolicyResponse deleteDeliveryDestinationPolicy(
            CloudWatchLogsClient client, DeleteDeliveryDestinationPolicyRequest request) {
        return client.deleteDeliveryDestinationPolicy(request);
    }

    public static DeleteDeliverySourceResponse deleteDeliverySource(
            CloudWatchLogsClient client, DeleteDeliverySourceRequest request) {
        return client.deleteDeliverySource(request);
    }

    public static DeleteDestinationResponse deleteDestination(
            CloudWatchLogsClient client, DeleteDestinationRequest request) {
        return client.deleteDestination(request);
    }

    public static DeleteSubscriptionFilterResponse deleteSubscriptionFilter(
            CloudWatchLogsClient client, DeleteSubscriptionFilterRequest request) {
        return client.deleteSubscriptionFilter(request);
    }

    public static List<Delivery> describeDeliveries(CloudWatchLogsClient client, DescribeDeliveriesRequest request) {
        return client.describeDeliveriesPaginator(request).stream()
                .flatMap(r -> r.deliveries().stream())
                .collect(Collectors.toList());
    }

    public static List<DeliveryDestination> describeDeliveryDestinations(
            CloudWatchLogsClient client, DescribeDeliveryDestinationsRequest request) {
        return client.describeDeliveryDestinationsPaginator(request).stream()
                .flatMap(r -> r.deliveryDestinations().stream())
                .collect(Collectors.toList());
    }

    public static List<DeliverySource> describeDeliverySources(
            CloudWatchLogsClient client, DescribeDeliverySourcesRequest request) {
        return client.describeDeliverySourcesPaginator(request).stream()
                .flatMap(r -> r.deliverySources().stream())
                .collect(Collectors.toList());
    }

    public static List<Destination> describeDestinations(
            CloudWatchLogsClient client, DescribeDestinationsRequest request) {
        return client.describeDestinationsPaginator(request).stream()
                .flatMap(r -> r.destinations().stream())
                .collect(Collectors.toList());
    }

    public static List<SubscriptionFilter> describeSubscriptionFilters(
            CloudWatchLogsClient client, DescribeSubscriptionFiltersRequest request) {
        return client.describeSubscriptionFiltersPaginator(request).stream()
                .flatMap(r -> r.subscriptionFilters().stream())
                .collect(Collectors.toList());
    }

    public static GetDeliveryResponse getDelivery(CloudWatchLogsClient client, GetDeliveryRequest request) {
        return client.getDelivery(request);
    }

    public static GetDeliveryDestinationResponse getDeliveryDestination(
            CloudWatchLogsClient client, GetDeliveryDestinationRequest request) {
        return client.getDeliveryDestination(request);
    }

    public static GetDeliveryDestinationPolicyResponse getDeliveryDestinationPolicy(
            CloudWatchLogsClient client, GetDeliveryDestinationPolicyRequest request) {
        return client.getDeliveryDestinationPolicy(request);
    }

    public static GetDeliverySourceResponse getDeliverySource(
            CloudWatchLogsClient client, GetDeliverySourceRequest request) {
        return client.getDeliverySource(request);
    }

    public static PutDeliveryDestinationResponse putDeliveryDestination(
            CloudWatchLogsClient client, PutDeliveryDestinationRequest request) {
        return client.putDeliveryDestination(request);
    }

    public static PutDeliveryDestinationPolicyResponse putDeliveryDestinationPolicy(
            CloudWatchLogsClient client, PutDeliveryDestinationPolicyRequest request) {
        return client.putDeliveryDestinationPolicy(request);
    }

    public static PutDeliverySourceResponse putDeliverySource(
            CloudWatchLogsClient client, PutDeliverySourceRequest request) {
        return client.putDeliverySource(request);
    }

    public static PutDestinationResponse putDestination(CloudWatchLogsClient client, PutDestinationRequest request) {
        return client.putDestination(request);
    }

    public static PutDestinationPolicyResponse putDestinationPolicy(
            CloudWatchLogsClient client, PutDestinationPolicyRequest request) {
        return client.putDestinationPolicy(request);
    }

    public static PutSubscriptionFilterResponse putSubscriptionFilter(
            CloudWatchLogsClient client, PutSubscriptionFilterRequest request) {
        return client.putSubscriptionFilter(request);
    }

    public static UpdateDeliveryConfigurationResponse updateDeliveryConfiguration(
            CloudWatchLogsClient client, UpdateDeliveryConfigurationRequest request) {
        return client.updateDeliveryConfiguration(request);
    }
}
