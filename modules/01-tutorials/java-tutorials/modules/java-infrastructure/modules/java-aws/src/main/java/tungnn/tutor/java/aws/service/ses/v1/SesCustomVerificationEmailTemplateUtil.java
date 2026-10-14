// SesCustomVerificationEmailTemplateUtil.java
package tungnn.tutor.java.aws.service.ses.v1;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

public final class SesCustomVerificationEmailTemplateUtil {

  private SesCustomVerificationEmailTemplateUtil() {}

  public static CreateCustomVerificationEmailTemplateResponse createCustomVerificationEmailTemplate(
      SesClient client, CreateCustomVerificationEmailTemplateRequest request) {
    return client.createCustomVerificationEmailTemplate(request);
  }

  public static DeleteCustomVerificationEmailTemplateResponse deleteCustomVerificationEmailTemplate(
      SesClient client, DeleteCustomVerificationEmailTemplateRequest request) {
    return client.deleteCustomVerificationEmailTemplate(request);
  }

  public static GetCustomVerificationEmailTemplateResponse getCustomVerificationEmailTemplate(
      SesClient client, GetCustomVerificationEmailTemplateRequest request) {
    return client.getCustomVerificationEmailTemplate(request);
  }

  public static List<CustomVerificationEmailTemplate> listCustomVerificationEmailTemplates(
      SesClient client, ListCustomVerificationEmailTemplatesRequest request) {
    return client.listCustomVerificationEmailTemplatesPaginator(request).stream()
        .flatMap(r -> r.customVerificationEmailTemplates().stream())
        .collect(Collectors.toList());
  }

  public static UpdateCustomVerificationEmailTemplateResponse updateCustomVerificationEmailTemplate(
      SesClient client, UpdateCustomVerificationEmailTemplateRequest request) {
    return client.updateCustomVerificationEmailTemplate(request);
  }
}
