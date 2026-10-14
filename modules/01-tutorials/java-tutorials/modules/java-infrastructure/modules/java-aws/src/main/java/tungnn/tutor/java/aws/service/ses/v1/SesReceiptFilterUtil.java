// SesReceiptFilterUtil.java
package tungnn.tutor.java.aws.service.ses.v1;

import java.util.List;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

public final class SesReceiptFilterUtil {

  private SesReceiptFilterUtil() {}

  public static CreateReceiptFilterResponse createReceiptFilter(
      SesClient client, CreateReceiptFilterRequest request) {
    return client.createReceiptFilter(request);
  }

  public static DeleteReceiptFilterResponse deleteReceiptFilter(
      SesClient client, DeleteReceiptFilterRequest request) {
    return client.deleteReceiptFilter(request);
  }

  public static List<ReceiptFilter> listReceiptFilters(
      SesClient client, ListReceiptFiltersRequest request) {
    return List.copyOf(client.listReceiptFilters(request).filters());
  }
}
