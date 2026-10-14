package tungnn.tutor.java.aws.service.ecr;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ecr.EcrClient;
import software.amazon.awssdk.services.ecr.model.*;

public final class EcrRepositoryUtil {

  private EcrRepositoryUtil() {}

  public static List<Repository> describeRepositories(
      EcrClient ecrClient, DescribeRepositoriesRequest request) {
    return ecrClient.describeRepositoriesPaginator(request).stream()
        .flatMap(r -> r.repositories().stream())
        .collect(Collectors.toList());
  }

  public static CreateRepositoryResponse createRepository(
      EcrClient ecrClient, CreateRepositoryRequest request) {
    return ecrClient.createRepository(request);
  }

  public static DeleteRepositoryResponse deleteRepository(
      EcrClient ecrClient, DeleteRepositoryRequest request) {
    return ecrClient.deleteRepository(request);
  }

  public static GetRepositoryPolicyResponse getRepositoryPolicy(
      EcrClient ecrClient, GetRepositoryPolicyRequest request) {
    return ecrClient.getRepositoryPolicy(request);
  }

  public static SetRepositoryPolicyResponse setRepositoryPolicy(
      EcrClient ecrClient, SetRepositoryPolicyRequest request) {
    return ecrClient.setRepositoryPolicy(request);
  }

  public static DeleteRepositoryPolicyResponse deleteRepositoryPolicy(
      EcrClient ecrClient, DeleteRepositoryPolicyRequest request) {
    return ecrClient.deleteRepositoryPolicy(request);
  }
}
