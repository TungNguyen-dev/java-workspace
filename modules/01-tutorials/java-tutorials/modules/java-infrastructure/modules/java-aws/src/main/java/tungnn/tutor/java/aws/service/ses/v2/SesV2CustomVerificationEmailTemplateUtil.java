// SesV2CustomVerificationEmailTemplateUtil.java
package tungnn.tutor.java.aws.service.ses.v2;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

public final class SesV2CustomVerificationEmailTemplateUtil {

  private SesV2CustomVerificationEmailTemplateUtil() {}

  public static CreateCustomVerificationEmailTemplateResponse createCustomVerificationEmailTemplate(
      SesV2Client client, CreateCustomVerificationEmailTemplateRequest request) {
    return client.createCustomVerificationEmailTemplate(request);
  }

  public static DeleteCustomVerificationEmailTemplateResponse deleteCustomVerificationEmailTemplate(
      SesV2Client client, DeleteCustomVerificationEmailTemplateRequest request) {
    return client.deleteCustomVerificationEmailTemplate(request);
  }

  public static GetCustomVerificationEmailTemplateResponse getCustomVerificationEmailTemplate(
      SesV2Client client, GetCustomVerificationEmailTemplateRequest request) {
    return client.getCustomVerificationEmailTemplate(request);
  }

  public static List<CustomVerificationEmailTemplateMetadata> listCustomVerificationEmailTemplates(
      SesV2Client client, ListCustomVerificationEmailTemplatesRequest request) {
    return client.listCustomVerificationEmailTemplatesPaginator(request).stream()
        .flatMap(r -> r.customVerificationEmailTemplates().stream())
        .collect(Collectors.toList());
  }

  public static UpdateCustomVerificationEmailTemplateResponse updateCustomVerificationEmailTemplate(
      SesV2Client client, UpdateCustomVerificationEmailTemplateRequest request) {
    return client.updateCustomVerificationEmailTemplate(request);
  }
}
