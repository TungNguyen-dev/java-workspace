package tungnn.tutor.java.aws.service.apigateway;

import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.*;

public final class ApiGatewayStageUtils {
    private ApiGatewayStageUtils() {}

    public static CreateStageResponse createStage(ApiGatewayClient client, CreateStageRequest request) {
        return client.createStage(request);
    }

    public static DeleteStageResponse deleteStage(ApiGatewayClient client, DeleteStageRequest request) {
        return client.deleteStage(request);
    }

    public static FlushStageCacheResponse flushStageCache(ApiGatewayClient client, FlushStageCacheRequest request) {
        return client.flushStageCache(request);
    }

    public static GetStageResponse getStage(ApiGatewayClient client, GetStageRequest request) {
        return client.getStage(request);
    }

    public static GetStagesResponse getStages(ApiGatewayClient client, GetStagesRequest request) {
        return client.getStages(request);
    }

    public static UpdateStageResponse updateStage(ApiGatewayClient client, UpdateStageRequest request) {
        return client.updateStage(request);
    }
}
