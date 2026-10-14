// SesTemplateUtil.java
package tungnn.tutor.java.aws.service.ses.v1;

import java.util.List;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

public final class SesTemplateUtil {

  private SesTemplateUtil() {}

  public static CreateTemplateResponse createTemplate(
      SesClient client, CreateTemplateRequest request) {
    return client.createTemplate(request);
  }

  public static DeleteTemplateResponse deleteTemplate(
      SesClient client, DeleteTemplateRequest request) {
    return client.deleteTemplate(request);
  }

  public static GetTemplateResponse getTemplate(SesClient client, GetTemplateRequest request) {
    return client.getTemplate(request);
  }

  public static List<TemplateMetadata> listTemplates(
      SesClient client, ListTemplatesRequest request) {
    return List.copyOf(client.listTemplates(request).templatesMetadata());
  }

  public static TestRenderTemplateResponse testRenderTemplate(
      SesClient client, TestRenderTemplateRequest request) {
    return client.testRenderTemplate(request);
  }

  public static UpdateTemplateResponse updateTemplate(
      SesClient client, UpdateTemplateRequest request) {
    return client.updateTemplate(request);
  }
}
