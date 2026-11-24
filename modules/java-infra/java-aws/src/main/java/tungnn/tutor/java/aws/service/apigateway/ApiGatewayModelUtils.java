package tungnn.tutor.java.aws.service.apigateway;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.*;

public final class ApiGatewayModelUtils {
    private ApiGatewayModelUtils() {}

    public static CreateModelResponse createModel(ApiGatewayClient client, CreateModelRequest request) {
        return client.createModel(request);
    }

    public static DeleteModelResponse deleteModel(ApiGatewayClient client, DeleteModelRequest request) {
        return client.deleteModel(request);
    }

    public static GetModelResponse getModel(ApiGatewayClient client, GetModelRequest request) {
        return client.getModel(request);
    }

    public static GetModelTemplateResponse getModelTemplate(ApiGatewayClient client, GetModelTemplateRequest request) {
        return client.getModelTemplate(request);
    }

    public static List<Model> getModels(ApiGatewayClient client, GetModelsRequest request) {
        return client.getModelsPaginator(request).stream()
                .flatMap(r -> r.items().stream())
                .collect(Collectors.toList());
    }

    public static UpdateModelResponse updateModel(ApiGatewayClient client, UpdateModelRequest request) {
        return client.updateModel(request);
    }
}
