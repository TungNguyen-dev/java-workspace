package tungnn.tutor.java.aws.service.sns;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

public final class SnsSubscriptionUtils {
    private SnsSubscriptionUtils() {}

    public static SubscribeResponse subscribe(SnsClient client, SubscribeRequest request) {
        return client.subscribe(request);
    }

    public static UnsubscribeResponse unsubscribe(SnsClient client, UnsubscribeRequest request) {
        return client.unsubscribe(request);
    }

    public static ConfirmSubscriptionResponse confirmSubscription(
            SnsClient client, ConfirmSubscriptionRequest request) {
        return client.confirmSubscription(request);
    }

    public static GetSubscriptionAttributesResponse getSubscriptionAttributes(
            SnsClient client, GetSubscriptionAttributesRequest request) {
        return client.getSubscriptionAttributes(request);
    }

    public static SetSubscriptionAttributesResponse setSubscriptionAttributes(
            SnsClient client, SetSubscriptionAttributesRequest request) {
        return client.setSubscriptionAttributes(request);
    }

    public static List<Subscription> listSubscriptions(SnsClient client, ListSubscriptionsRequest request) {
        return client.listSubscriptionsPaginator(request).stream()
                .flatMap(r -> r.subscriptions().stream())
                .collect(Collectors.toList());
    }

    public static List<Subscription> listSubscriptionsByTopic(
            SnsClient client, ListSubscriptionsByTopicRequest request) {
        return client.listSubscriptionsByTopicPaginator(request).stream()
                .flatMap(r -> r.subscriptions().stream())
                .collect(Collectors.toList());
    }
}
