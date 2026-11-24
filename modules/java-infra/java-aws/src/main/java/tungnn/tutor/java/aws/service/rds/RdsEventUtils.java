package tungnn.tutor.java.aws.service.rds;

import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.*;

public final class RdsEventUtils {
    private RdsEventUtils() {}

    public static AddSourceIdentifierToSubscriptionResponse addSourceIdentifierToSubscription(
            RdsClient client, AddSourceIdentifierToSubscriptionRequest request) {
        return client.addSourceIdentifierToSubscription(request);
    }

    public static CreateEventSubscriptionResponse createEventSubscription(
            RdsClient client, CreateEventSubscriptionRequest request) {
        return client.createEventSubscription(request);
    }

    public static DeleteEventSubscriptionResponse deleteEventSubscription(
            RdsClient client, DeleteEventSubscriptionRequest request) {
        return client.deleteEventSubscription(request);
    }

    public static DescribeEventCategoriesResponse describeEventCategories(
            RdsClient client, DescribeEventCategoriesRequest request) {
        return client.describeEventCategories(request);
    }

    public static DescribeEventSubscriptionsResponse describeEventSubscriptions(
            RdsClient client, DescribeEventSubscriptionsRequest request) {
        return client.describeEventSubscriptions(request);
    }

    public static DescribeEventsResponse describeEvents(RdsClient client, DescribeEventsRequest request) {
        return client.describeEvents(request);
    }

    public static ModifyEventSubscriptionResponse modifyEventSubscription(
            RdsClient client, ModifyEventSubscriptionRequest request) {
        return client.modifyEventSubscription(request);
    }

    public static RemoveSourceIdentifierFromSubscriptionResponse removeSourceIdentifierFromSubscription(
            RdsClient client, RemoveSourceIdentifierFromSubscriptionRequest request) {
        return client.removeSourceIdentifierFromSubscription(request);
    }
}
