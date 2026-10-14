package tungnn.tutor.java.aws.service.ssm;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;

public final class SsmParameterUtil {

  private SsmParameterUtil() {}

  public static DeleteParameterResponse deleteParameter(
      SsmClient client, DeleteParameterRequest request) {
    return client.deleteParameter(request);
  }

  public static DeleteParametersResponse deleteParameters(
      SsmClient client, DeleteParametersRequest request) {
    return client.deleteParameters(request);
  }

  public static List<ParameterMetadata> describeParameters(
      SsmClient client, DescribeParametersRequest request) {
    return client.describeParametersPaginator(request).stream()
        .flatMap(r -> r.parameters().stream())
        .collect(Collectors.toList());
  }

  public static GetParameterResponse getParameter(SsmClient client, GetParameterRequest request) {
    return client.getParameter(request);
  }

  public static List<Parameter> getParameters(SsmClient client, GetParametersRequest request) {
    return List.copyOf(client.getParameters(request).parameters());
  }

  public static List<ParameterHistory> getParameterHistory(
      SsmClient client, GetParameterHistoryRequest request) {
    return client.getParameterHistoryPaginator(request).stream()
        .flatMap(r -> r.parameters().stream())
        .collect(Collectors.toList());
  }

  public static List<Parameter> getParametersByPath(
      SsmClient client, GetParametersByPathRequest request) {
    return client.getParametersByPathPaginator(request).stream()
        .flatMap(r -> r.parameters().stream())
        .collect(Collectors.toList());
  }

  public static LabelParameterVersionResponse labelParameterVersion(
      SsmClient client, LabelParameterVersionRequest request) {
    return client.labelParameterVersion(request);
  }

  public static PutParameterResponse putParameter(SsmClient client, PutParameterRequest request) {
    return client.putParameter(request);
  }

  public static UnlabelParameterVersionResponse unlabelParameterVersion(
      SsmClient client, UnlabelParameterVersionRequest request) {
    return client.unlabelParameterVersion(request);
  }
}
