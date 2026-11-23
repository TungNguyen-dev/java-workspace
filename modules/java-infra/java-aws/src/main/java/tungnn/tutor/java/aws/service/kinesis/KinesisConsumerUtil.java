package tungnn.tutor.java.aws.service.kinesis;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.*;

public final class KinesisConsumerUtil {
  private KinesisConsumerUtil() {}

  public static DeregisterStreamConsumerResponse deregisterStreamConsumer(
      KinesisClient client, DeregisterStreamConsumerRequest request) {
    return client.deregisterStreamConsumer(request);
  }

  public static DescribeStreamConsumerResponse describeStreamConsumer(
      KinesisClient client, DescribeStreamConsumerRequest request) {
    return client.describeStreamConsumer(request);
  }

  public static List<Consumer> listStreamConsumers(
      KinesisClient client, ListStreamConsumersRequest request) {
    return client.listStreamConsumersPaginator(request).stream()
        .flatMap(r -> r.consumers().stream())
        .collect(Collectors.toList());
  }

  public static RegisterStreamConsumerResponse registerStreamConsumer(
      KinesisClient client, RegisterStreamConsumerRequest request) {
    return client.registerStreamConsumer(request);
  }
}
