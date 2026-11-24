package tungnn.tutor.java.aws.service.kms;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.*;

public final class KmsGrantUtils {
    private KmsGrantUtils() {}

    public static CreateGrantResponse createGrant(KmsClient client, CreateGrantRequest request) {
        return client.createGrant(request);
    }

    public static List<GrantListEntry> listGrants(KmsClient client, ListGrantsRequest request) {
        return client.listGrantsPaginator(request).stream()
                .flatMap(r -> r.grants().stream())
                .collect(Collectors.toList());
    }

    public static List<GrantListEntry> listRetirableGrants(KmsClient client, ListRetirableGrantsRequest request) {
        return client.listRetirableGrantsPaginator(request).stream()
                .flatMap(r -> r.grants().stream())
                .collect(Collectors.toList());
    }

    public static RetireGrantResponse retireGrant(KmsClient client, RetireGrantRequest request) {
        return client.retireGrant(request);
    }

    public static RevokeGrantResponse revokeGrant(KmsClient client, RevokeGrantRequest request) {
        return client.revokeGrant(request);
    }
}
