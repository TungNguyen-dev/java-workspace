package tungnn.tutor.java.aws.service.lambda;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;

public final class LambdaUrlConfigUtil {
    private LambdaUrlConfigUtil() {}

    public static CreateFunctionUrlConfigResponse createFunctionUrlConfig(
            LambdaClient client, CreateFunctionUrlConfigRequest request) {
        return client.createFunctionUrlConfig(request);
    }

    public static DeleteFunctionUrlConfigResponse deleteFunctionUrlConfig(
            LambdaClient client, DeleteFunctionUrlConfigRequest request) {
        return client.deleteFunctionUrlConfig(request);
    }

    public static GetFunctionUrlConfigResponse getFunctionUrlConfig(
            LambdaClient client, GetFunctionUrlConfigRequest request) {
        return client.getFunctionUrlConfig(request);
    }

    public static List<FunctionUrlConfig> listFunctionUrlConfigs(
            LambdaClient client, ListFunctionUrlConfigsRequest request) {
        return client.listFunctionUrlConfigsPaginator(request).stream()
                .flatMap(r -> r.functionUrlConfigs().stream())
                .collect(Collectors.toList());
    }

    public static UpdateFunctionUrlConfigResponse updateFunctionUrlConfig(
            LambdaClient client, UpdateFunctionUrlConfigRequest request) {
        return client.updateFunctionUrlConfig(request);
    }
}
