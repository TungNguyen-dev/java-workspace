package tungnn.tutor.java.aws.service.sqs;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

public final class SqsTagUtil {
  private SqsTagUtil() {}

  public static TagQueueResponse tagQueue(SqsClient client, TagQueueRequest request) {
    return client.tagQueue(request);
  }

  public static UntagQueueResponse untagQueue(SqsClient client, UntagQueueRequest request) {
    return client.untagQueue(request);
  }

  public static ListQueueTagsResponse listQueueTags(
      SqsClient client, ListQueueTagsRequest request) {
    return client.listQueueTags(request);
  }
}
