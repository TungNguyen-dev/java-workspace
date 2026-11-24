package tungnn.tutor.java.aws.service.kms;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.*;

public final class KmsAliasUtil {
    private KmsAliasUtil() {}

    public static CreateAliasResponse createAlias(KmsClient client, CreateAliasRequest request) {
        return client.createAlias(request);
    }

    public static DeleteAliasResponse deleteAlias(KmsClient client, DeleteAliasRequest request) {
        return client.deleteAlias(request);
    }

    public static UpdateAliasResponse updateAlias(KmsClient client, UpdateAliasRequest request) {
        return client.updateAlias(request);
    }

    public static List<AliasListEntry> listAliases(KmsClient client, ListAliasesRequest request) {
        return client.listAliasesPaginator(request).stream()
                .flatMap(r -> r.aliases().stream())
                .collect(Collectors.toList());
    }
}
