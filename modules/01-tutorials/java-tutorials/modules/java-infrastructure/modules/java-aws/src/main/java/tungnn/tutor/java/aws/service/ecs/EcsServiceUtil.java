package tungnn.tutor.java.aws.service.ecs;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

public final class EcsServiceUtil {

  private EcsServiceUtil() {}

  public static CreateServiceResponse createService(
      EcsClient client, CreateServiceRequest request) {
    return client.createService(request);
  }

  public static DeleteServiceResponse deleteService(
      EcsClient client, DeleteServiceRequest request) {
    return client.deleteService(request);
  }

  public static UpdateServiceResponse updateService(
      EcsClient client, UpdateServiceRequest request) {
    return client.updateService(request);
  }

  public static UpdateServicePrimaryTaskSetResponse updateServicePrimaryTaskSet(
      EcsClient client, UpdateServicePrimaryTaskSetRequest request) {
    return client.updateServicePrimaryTaskSet(request);
  }

  public static List<Service> describeServices(EcsClient client, DescribeServicesRequest request) {
    return List.copyOf(client.describeServices(request).services());
  }

  public static List<String> listServices(EcsClient client, ListServicesRequest request) {
    return client.listServicesPaginator(request).stream()
        .flatMap(r -> r.serviceArns().stream())
        .collect(Collectors.toList());
  }

  public static List<String> listServicesByNamespace(
      EcsClient client, ListServicesByNamespaceRequest request) {
    return client.listServicesByNamespacePaginator(request).stream()
        .flatMap(r -> r.serviceArns().stream())
        .collect(Collectors.toList());
  }
}
