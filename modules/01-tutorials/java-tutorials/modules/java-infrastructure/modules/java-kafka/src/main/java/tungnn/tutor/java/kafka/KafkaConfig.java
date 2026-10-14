package tungnn.tutor.java.kafka;

import java.util.Properties;

public final class KafkaConfig {

  private KafkaConfig() {}

  public static Properties consumerProps(String bootstrapServers, String groupId) {
    var props = new Properties();
    props.put("bootstrap.servers", bootstrapServers);
    props.put("group.id", groupId);

    // Recommended defaults
    props.put("enable.auto.commit", "false");
    props.put("auto.offset.reset", "earliest");

    return props;
  }

  public static Properties producerProps(String bootstrapServers) {
    var props = new Properties();
    props.put("bootstrap.servers", bootstrapServers);

    // Recommended defaults
    props.put("acks", "all"); // durability
    props.put("retries", 3);
    props.put("linger.ms", 5); // batching
    props.put("enable.idempotence", "true"); // avoid duplicates

    return props;
  }
}
