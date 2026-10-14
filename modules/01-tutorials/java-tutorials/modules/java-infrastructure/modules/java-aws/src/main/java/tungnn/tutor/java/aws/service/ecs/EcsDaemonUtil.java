package tungnn.tutor.java.aws.service.ecs;

import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

public final class EcsDaemonUtil {

  private EcsDaemonUtil() {}

  public static CreateDaemonResponse createDaemon(EcsClient client, CreateDaemonRequest request) {
    return client.createDaemon(request);
  }

  public static DeleteDaemonResponse deleteDaemon(EcsClient client, DeleteDaemonRequest request) {
    return client.deleteDaemon(request);
  }

  public static UpdateDaemonResponse updateDaemon(EcsClient client, UpdateDaemonRequest request) {
    return client.updateDaemon(request);
  }

  public static DescribeDaemonResponse describeDaemon(
      EcsClient client, DescribeDaemonRequest request) {
    return client.describeDaemon(request);
  }

  public static DescribeDaemonDeploymentsResponse describeDaemonDeployments(
      EcsClient client, DescribeDaemonDeploymentsRequest request) {
    return client.describeDaemonDeployments(request);
  }

  public static DescribeDaemonRevisionsResponse describeDaemonRevisions(
      EcsClient client, DescribeDaemonRevisionsRequest request) {
    return client.describeDaemonRevisions(request);
  }

  public static ListDaemonsResponse listDaemons(EcsClient client, ListDaemonsRequest request) {
    return client.listDaemons(request);
  }

  public static ListDaemonDeploymentsResponse listDaemonDeployments(
      EcsClient client, ListDaemonDeploymentsRequest request) {
    return client.listDaemonDeployments(request);
  }

  public static RegisterDaemonTaskDefinitionResponse registerDaemonTaskDefinition(
      EcsClient client, RegisterDaemonTaskDefinitionRequest request) {
    return client.registerDaemonTaskDefinition(request);
  }

  public static DeleteDaemonTaskDefinitionResponse deleteDaemonTaskDefinition(
      EcsClient client, DeleteDaemonTaskDefinitionRequest request) {
    return client.deleteDaemonTaskDefinition(request);
  }

  public static DescribeDaemonTaskDefinitionResponse describeDaemonTaskDefinition(
      EcsClient client, DescribeDaemonTaskDefinitionRequest request) {
    return client.describeDaemonTaskDefinition(request);
  }

  public static ListDaemonTaskDefinitionsResponse listDaemonTaskDefinitions(
      EcsClient client, ListDaemonTaskDefinitionsRequest request) {
    return client.listDaemonTaskDefinitions(request);
  }
}
