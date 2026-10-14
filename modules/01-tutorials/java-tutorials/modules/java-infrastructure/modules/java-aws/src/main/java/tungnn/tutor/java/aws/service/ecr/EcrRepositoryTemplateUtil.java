package tungnn.tutor.java.aws.service.ecr;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ecr.EcrClient;
import software.amazon.awssdk.services.ecr.model.*;

public final class EcrRepositoryTemplateUtil {

  private EcrRepositoryTemplateUtil() {}

  public static List<RepositoryCreationTemplate> describeRepositoryCreationTemplates(
      EcrClient ecrClient, DescribeRepositoryCreationTemplatesRequest request) {
    return ecrClient.describeRepositoryCreationTemplatesPaginator(request).stream()
        .flatMap(r -> r.repositoryCreationTemplates().stream())
        .collect(Collectors.toList());
  }

  public static CreateRepositoryCreationTemplateResponse createRepositoryCreationTemplate(
      EcrClient ecrClient, CreateRepositoryCreationTemplateRequest request) {
    return ecrClient.createRepositoryCreationTemplate(request);
  }

  public static UpdateRepositoryCreationTemplateResponse updateRepositoryCreationTemplate(
      EcrClient ecrClient, UpdateRepositoryCreationTemplateRequest request) {
    return ecrClient.updateRepositoryCreationTemplate(request);
  }

  public static DeleteRepositoryCreationTemplateResponse deleteRepositoryCreationTemplate(
      EcrClient ecrClient, DeleteRepositoryCreationTemplateRequest request) {
    return ecrClient.deleteRepositoryCreationTemplate(request);
  }
}
