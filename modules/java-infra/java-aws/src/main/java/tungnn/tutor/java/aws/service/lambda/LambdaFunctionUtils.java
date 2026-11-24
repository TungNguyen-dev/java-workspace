package tungnn.tutor.java.aws.service.lambda;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;

public final class LambdaFunctionUtils {
    private LambdaFunctionUtils() {}

    public static CheckpointDurableExecutionResponse checkpointDurableExecution(
            LambdaClient client, CheckpointDurableExecutionRequest request) {
        return client.checkpointDurableExecution(request);
    }

    public static CreateCapacityProviderResponse createCapacityProvider(
            LambdaClient client, CreateCapacityProviderRequest request) {
        return client.createCapacityProvider(request);
    }

    public static CreateCodeSigningConfigResponse createCodeSigningConfig(
            LambdaClient client, CreateCodeSigningConfigRequest request) {
        return client.createCodeSigningConfig(request);
    }

    public static CreateFunctionResponse createFunction(LambdaClient client, CreateFunctionRequest request) {
        return client.createFunction(request);
    }

    public static DeleteCapacityProviderResponse deleteCapacityProvider(
            LambdaClient client, DeleteCapacityProviderRequest request) {
        return client.deleteCapacityProvider(request);
    }

    public static DeleteCodeSigningConfigResponse deleteCodeSigningConfig(
            LambdaClient client, DeleteCodeSigningConfigRequest request) {
        return client.deleteCodeSigningConfig(request);
    }

    public static DeleteFunctionResponse deleteFunction(LambdaClient client, DeleteFunctionRequest request) {
        return client.deleteFunction(request);
    }

    public static DeleteFunctionCodeSigningConfigResponse deleteFunctionCodeSigningConfig(
            LambdaClient client, DeleteFunctionCodeSigningConfigRequest request) {
        return client.deleteFunctionCodeSigningConfig(request);
    }

    public static DeleteFunctionEventInvokeConfigResponse deleteFunctionEventInvokeConfig(
            LambdaClient client, DeleteFunctionEventInvokeConfigRequest request) {
        return client.deleteFunctionEventInvokeConfig(request);
    }

    public static GetAccountSettingsResponse getAccountSettings(
            LambdaClient client, GetAccountSettingsRequest request) {
        return client.getAccountSettings(request);
    }

    public static GetCapacityProviderResponse getCapacityProvider(
            LambdaClient client, GetCapacityProviderRequest request) {
        return client.getCapacityProvider(request);
    }

    public static GetCodeSigningConfigResponse getCodeSigningConfig(
            LambdaClient client, GetCodeSigningConfigRequest request) {
        return client.getCodeSigningConfig(request);
    }

    public static GetDurableExecutionResponse getDurableExecution(
            LambdaClient client, GetDurableExecutionRequest request) {
        return client.getDurableExecution(request);
    }

    public static List<Event> getDurableExecutionHistory(
            LambdaClient client, GetDurableExecutionHistoryRequest request) {
        return client.getDurableExecutionHistoryPaginator(request).stream()
                .flatMap(r -> r.events().stream())
                .collect(Collectors.toList());
    }

    public static List<Operation> getDurableExecutionState(
            LambdaClient client, GetDurableExecutionStateRequest request) {
        return client.getDurableExecutionStatePaginator(request).stream()
                .flatMap(r -> r.operations().stream())
                .collect(Collectors.toList());
    }

    public static GetFunctionResponse getFunction(LambdaClient client, GetFunctionRequest request) {
        return client.getFunction(request);
    }

    public static GetFunctionCodeSigningConfigResponse getFunctionCodeSigningConfig(
            LambdaClient client, GetFunctionCodeSigningConfigRequest request) {
        return client.getFunctionCodeSigningConfig(request);
    }

    public static GetFunctionConfigurationResponse getFunctionConfiguration(
            LambdaClient client, GetFunctionConfigurationRequest request) {
        return client.getFunctionConfiguration(request);
    }

    public static GetFunctionEventInvokeConfigResponse getFunctionEventInvokeConfig(
            LambdaClient client, GetFunctionEventInvokeConfigRequest request) {
        return client.getFunctionEventInvokeConfig(request);
    }

    public static GetFunctionRecursionConfigResponse getFunctionRecursionConfig(
            LambdaClient client, GetFunctionRecursionConfigRequest request) {
        return client.getFunctionRecursionConfig(request);
    }

    public static GetFunctionScalingConfigResponse getFunctionScalingConfig(
            LambdaClient client, GetFunctionScalingConfigRequest request) {
        return client.getFunctionScalingConfig(request);
    }

    public static GetRuntimeManagementConfigResponse getRuntimeManagementConfig(
            LambdaClient client, GetRuntimeManagementConfigRequest request) {
        return client.getRuntimeManagementConfig(request);
    }

    public static InvokeResponse invoke(LambdaClient client, InvokeRequest request) {
        return client.invoke(request);
    }

    public static List<CapacityProvider> listCapacityProviders(
            LambdaClient client, ListCapacityProvidersRequest request) {
        return client.listCapacityProvidersPaginator(request).stream()
                .flatMap(r -> r.capacityProviders().stream())
                .collect(Collectors.toList());
    }

    public static List<CodeSigningConfig> listCodeSigningConfigs(
            LambdaClient client, ListCodeSigningConfigsRequest request) {
        return client.listCodeSigningConfigsPaginator(request).stream()
                .flatMap(r -> r.codeSigningConfigs().stream())
                .collect(Collectors.toList());
    }

    public static List<Execution> listDurableExecutionsByFunction(
            LambdaClient client, ListDurableExecutionsByFunctionRequest request) {
        return client.listDurableExecutionsByFunctionPaginator(request).stream()
                .flatMap(r -> r.durableExecutions().stream())
                .collect(Collectors.toList());
    }

    public static List<FunctionEventInvokeConfig> listFunctionEventInvokeConfigs(
            LambdaClient client, ListFunctionEventInvokeConfigsRequest request) {
        return client.listFunctionEventInvokeConfigsPaginator(request).stream()
                .flatMap(r -> r.functionEventInvokeConfigs().stream())
                .collect(Collectors.toList());
    }

    public static List<FunctionConfiguration> listFunctions(LambdaClient client, ListFunctionsRequest request) {
        return client.listFunctionsPaginator(request).stream()
                .flatMap(r -> r.functions().stream())
                .collect(Collectors.toList());
    }

    public static List<String> listFunctionsByCodeSigningConfig(
            LambdaClient client, ListFunctionsByCodeSigningConfigRequest request) {
        return client.listFunctionsByCodeSigningConfigPaginator(request).stream()
                .flatMap(r -> r.functionArns().stream())
                .collect(Collectors.toList());
    }

    public static PutFunctionCodeSigningConfigResponse putFunctionCodeSigningConfig(
            LambdaClient client, PutFunctionCodeSigningConfigRequest request) {
        return client.putFunctionCodeSigningConfig(request);
    }

    public static PutFunctionEventInvokeConfigResponse putFunctionEventInvokeConfig(
            LambdaClient client, PutFunctionEventInvokeConfigRequest request) {
        return client.putFunctionEventInvokeConfig(request);
    }

    public static PutFunctionRecursionConfigResponse putFunctionRecursionConfig(
            LambdaClient client, PutFunctionRecursionConfigRequest request) {
        return client.putFunctionRecursionConfig(request);
    }

    public static PutFunctionScalingConfigResponse putFunctionScalingConfig(
            LambdaClient client, PutFunctionScalingConfigRequest request) {
        return client.putFunctionScalingConfig(request);
    }

    public static PutRuntimeManagementConfigResponse putRuntimeManagementConfig(
            LambdaClient client, PutRuntimeManagementConfigRequest request) {
        return client.putRuntimeManagementConfig(request);
    }

    public static SendDurableExecutionCallbackFailureResponse sendDurableExecutionCallbackFailure(
            LambdaClient client, SendDurableExecutionCallbackFailureRequest request) {
        return client.sendDurableExecutionCallbackFailure(request);
    }

    public static SendDurableExecutionCallbackHeartbeatResponse sendDurableExecutionCallbackHeartbeat(
            LambdaClient client, SendDurableExecutionCallbackHeartbeatRequest request) {
        return client.sendDurableExecutionCallbackHeartbeat(request);
    }

    public static SendDurableExecutionCallbackSuccessResponse sendDurableExecutionCallbackSuccess(
            LambdaClient client, SendDurableExecutionCallbackSuccessRequest request) {
        return client.sendDurableExecutionCallbackSuccess(request);
    }

    public static StopDurableExecutionResponse stopDurableExecution(
            LambdaClient client, StopDurableExecutionRequest request) {
        return client.stopDurableExecution(request);
    }

    public static UpdateCapacityProviderResponse updateCapacityProvider(
            LambdaClient client, UpdateCapacityProviderRequest request) {
        return client.updateCapacityProvider(request);
    }

    public static UpdateCodeSigningConfigResponse updateCodeSigningConfig(
            LambdaClient client, UpdateCodeSigningConfigRequest request) {
        return client.updateCodeSigningConfig(request);
    }

    public static UpdateFunctionCodeResponse updateFunctionCode(
            LambdaClient client, UpdateFunctionCodeRequest request) {
        return client.updateFunctionCode(request);
    }

    public static UpdateFunctionConfigurationResponse updateFunctionConfiguration(
            LambdaClient client, UpdateFunctionConfigurationRequest request) {
        return client.updateFunctionConfiguration(request);
    }

    public static UpdateFunctionEventInvokeConfigResponse updateFunctionEventInvokeConfig(
            LambdaClient client, UpdateFunctionEventInvokeConfigRequest request) {
        return client.updateFunctionEventInvokeConfig(request);
    }
}
