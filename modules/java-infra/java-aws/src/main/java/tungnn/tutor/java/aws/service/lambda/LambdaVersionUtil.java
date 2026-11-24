package tungnn.tutor.java.aws.service.lambda;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;

public final class LambdaVersionUtil {
    private LambdaVersionUtil() {}

    public static List<FunctionVersionsByCapacityProviderListItem> listFunctionVersionsByCapacityProvider(
            LambdaClient client, ListFunctionVersionsByCapacityProviderRequest request) {
        return client.listFunctionVersionsByCapacityProviderPaginator(request).stream()
                .flatMap(r -> r.functionVersions().stream())
                .collect(Collectors.toList());
    }

    public static List<FunctionConfiguration> listVersionsByFunction(
            LambdaClient client, ListVersionsByFunctionRequest request) {
        return client.listVersionsByFunctionPaginator(request).stream()
                .flatMap(r -> r.versions().stream())
                .collect(Collectors.toList());
    }

    public static PublishVersionResponse publishVersion(LambdaClient client, PublishVersionRequest request) {
        return client.publishVersion(request);
    }
}
