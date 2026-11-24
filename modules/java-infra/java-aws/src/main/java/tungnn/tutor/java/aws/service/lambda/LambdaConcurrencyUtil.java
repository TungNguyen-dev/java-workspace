package tungnn.tutor.java.aws.service.lambda;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;

public final class LambdaConcurrencyUtil {
    private LambdaConcurrencyUtil() {}

    public static DeleteFunctionConcurrencyResponse deleteFunctionConcurrency(
            LambdaClient client, DeleteFunctionConcurrencyRequest request) {
        return client.deleteFunctionConcurrency(request);
    }

    public static DeleteProvisionedConcurrencyConfigResponse deleteProvisionedConcurrencyConfig(
            LambdaClient client, DeleteProvisionedConcurrencyConfigRequest request) {
        return client.deleteProvisionedConcurrencyConfig(request);
    }

    public static GetFunctionConcurrencyResponse getFunctionConcurrency(
            LambdaClient client, GetFunctionConcurrencyRequest request) {
        return client.getFunctionConcurrency(request);
    }

    public static GetProvisionedConcurrencyConfigResponse getProvisionedConcurrencyConfig(
            LambdaClient client, GetProvisionedConcurrencyConfigRequest request) {
        return client.getProvisionedConcurrencyConfig(request);
    }

    public static List<ProvisionedConcurrencyConfigListItem> listProvisionedConcurrencyConfigs(
            LambdaClient client, ListProvisionedConcurrencyConfigsRequest request) {
        return client.listProvisionedConcurrencyConfigsPaginator(request).stream()
                .flatMap(r -> r.provisionedConcurrencyConfigs().stream())
                .collect(Collectors.toList());
    }

    public static PutFunctionConcurrencyResponse putFunctionConcurrency(
            LambdaClient client, PutFunctionConcurrencyRequest request) {
        return client.putFunctionConcurrency(request);
    }

    public static PutProvisionedConcurrencyConfigResponse putProvisionedConcurrencyConfig(
            LambdaClient client, PutProvisionedConcurrencyConfigRequest request) {
        return client.putProvisionedConcurrencyConfig(request);
    }
}
