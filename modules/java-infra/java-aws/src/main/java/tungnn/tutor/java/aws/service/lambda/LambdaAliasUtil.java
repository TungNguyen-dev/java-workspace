package tungnn.tutor.java.aws.service.lambda;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;

public final class LambdaAliasUtil {
    private LambdaAliasUtil() {}

    public static CreateAliasResponse createAlias(LambdaClient client, CreateAliasRequest request) {
        return client.createAlias(request);
    }

    public static DeleteAliasResponse deleteAlias(LambdaClient client, DeleteAliasRequest request) {
        return client.deleteAlias(request);
    }

    public static GetAliasResponse getAlias(LambdaClient client, GetAliasRequest request) {
        return client.getAlias(request);
    }

    public static List<AliasConfiguration> listAliases(LambdaClient client, ListAliasesRequest request) {
        return client.listAliasesPaginator(request).stream()
                .flatMap(r -> r.aliases().stream())
                .collect(Collectors.toList());
    }

    public static UpdateAliasResponse updateAlias(LambdaClient client, UpdateAliasRequest request) {
        return client.updateAlias(request);
    }
}
