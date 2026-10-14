// SesV2EmailTemplateUtil.java
package tungnn.tutor.java.aws.service.ses.v2;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

public final class SesV2EmailTemplateUtil {

  private SesV2EmailTemplateUtil() {}

  public static CreateEmailTemplateResponse createEmailTemplate(
      SesV2Client client, CreateEmailTemplateRequest request) {
    return client.createEmailTemplate(request);
  }

  public static DeleteEmailTemplateResponse deleteEmailTemplate(
      SesV2Client client, DeleteEmailTemplateRequest request) {
    return client.deleteEmailTemplate(request);
  }

  public static GetEmailTemplateResponse getEmailTemplate(
      SesV2Client client, GetEmailTemplateRequest request) {
    return client.getEmailTemplate(request);
  }

  public static List<EmailTemplateMetadata> listEmailTemplates(
      SesV2Client client, ListEmailTemplatesRequest request) {
    return client.listEmailTemplatesPaginator(request).stream()
        .flatMap(r -> r.templatesMetadata().stream())
        .collect(Collectors.toList());
  }

  public static TestRenderEmailTemplateResponse testRenderEmailTemplate(
      SesV2Client client, TestRenderEmailTemplateRequest request) {
    return client.testRenderEmailTemplate(request);
  }

  public static UpdateEmailTemplateResponse updateEmailTemplate(
      SesV2Client client, UpdateEmailTemplateRequest request) {
    return client.updateEmailTemplate(request);
  }
}
