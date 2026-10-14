package tungnn.tutor.java.aws.service.ses.v1;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

public final class SesReceiptRuleSetUtil {

  private SesReceiptRuleSetUtil() {}

  public static CloneReceiptRuleSetResponse cloneReceiptRuleSet(
      SesClient client, CloneReceiptRuleSetRequest request) {
    return client.cloneReceiptRuleSet(request);
  }

  public static CreateReceiptRuleSetResponse createReceiptRuleSet(
      SesClient client, CreateReceiptRuleSetRequest request) {
    return client.createReceiptRuleSet(request);
  }

  public static DeleteReceiptRuleSetResponse deleteReceiptRuleSet(
      SesClient client, DeleteReceiptRuleSetRequest request) {
    return client.deleteReceiptRuleSet(request);
  }

  public static DescribeActiveReceiptRuleSetResponse describeActiveReceiptRuleSet(
      SesClient client, DescribeActiveReceiptRuleSetRequest request) {
    return client.describeActiveReceiptRuleSet(request);
  }

  public static DescribeReceiptRuleSetResponse describeReceiptRuleSet(
      SesClient client, DescribeReceiptRuleSetRequest request) {
    return client.describeReceiptRuleSet(request);
  }

  public static List<ReceiptRuleSetMetadata> listReceiptRuleSets(
      SesClient client, ListReceiptRuleSetsRequest request) {
    return Stream.iterate(
            client.listReceiptRuleSets(request),
            Objects::nonNull,
            prev ->
                prev.nextToken() == null
                    ? null
                    : client.listReceiptRuleSets(
                        request.toBuilder().nextToken(prev.nextToken()).build()))
        .flatMap(r -> r.ruleSets().stream())
        .collect(Collectors.toList());
  }

  public static ReorderReceiptRuleSetResponse reorderReceiptRuleSet(
      SesClient client, ReorderReceiptRuleSetRequest request) {
    return client.reorderReceiptRuleSet(request);
  }

  public static SetActiveReceiptRuleSetResponse setActiveReceiptRuleSet(
      SesClient client, SetActiveReceiptRuleSetRequest request) {
    return client.setActiveReceiptRuleSet(request);
  }
}
