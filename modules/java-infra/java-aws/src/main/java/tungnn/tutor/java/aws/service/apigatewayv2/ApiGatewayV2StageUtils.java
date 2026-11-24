package tungnn.tutor.java.aws.service.apigatewayv2;

import software.amazon.awssdk.services.apigatewayv2.ApiGatewayV2Client;
import software.amazon.awssdk.services.apigatewayv2.model.*;

public final class ApiGatewayV2StageUtils {
    private ApiGatewayV2StageUtils() {}

    public static CreateStageResponse createStage(ApiGatewayV2Client client, CreateStageRequest request) {
        return client.createStage(request);
    }

    public static DeleteAccessLogSettingsResponse deleteAccessLogSettings(
            ApiGatewayV2Client client, DeleteAccessLogSettingsRequest request) {
        return client.deleteAccessLogSettings(request);
    }

    public static DeleteStageResponse deleteStage(ApiGatewayV2Client client, DeleteStageRequest request) {
        return client.deleteStage(request);
    }

    public static GetStageResponse getStage(ApiGatewayV2Client client, GetStageRequest request) {
        return client.getStage(request);
    }

    public static GetStagesResponse getStages(ApiGatewayV2Client client, GetStagesRequest request) {
        return client.getStages(request);
    }

    public static UpdateStageResponse updateStage(ApiGatewayV2Client client, UpdateStageRequest request) {
        return client.updateStage(request);
    }
}
