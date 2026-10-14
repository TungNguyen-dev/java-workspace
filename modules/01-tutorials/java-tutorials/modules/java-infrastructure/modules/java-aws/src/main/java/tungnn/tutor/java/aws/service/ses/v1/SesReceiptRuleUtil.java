// SesReceiptRuleUtil.java
package tungnn.tutor.java.aws.service.ses.v1;

import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

public final class SesReceiptRuleUtil {

  private SesReceiptRuleUtil() {}

  public static CreateReceiptRuleResponse createReceiptRule(
      SesClient client, CreateReceiptRuleRequest request) {
    return client.createReceiptRule(request);
  }

  public static DeleteReceiptRuleResponse deleteReceiptRule(
      SesClient client, DeleteReceiptRuleRequest request) {
    return client.deleteReceiptRule(request);
  }

  public static DescribeReceiptRuleResponse describeReceiptRule(
      SesClient client, DescribeReceiptRuleRequest request) {
    return client.describeReceiptRule(request);
  }

  public static SetReceiptRulePositionResponse setReceiptRulePosition(
      SesClient client, SetReceiptRulePositionRequest request) {
    return client.setReceiptRulePosition(request);
  }

  public static UpdateReceiptRuleResponse updateReceiptRule(
      SesClient client, UpdateReceiptRuleRequest request) {
    return client.updateReceiptRule(request);
  }
}
