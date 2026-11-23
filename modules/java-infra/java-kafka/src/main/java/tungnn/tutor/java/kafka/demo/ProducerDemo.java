package tungnn.tutor.java.kafka.demo;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import tungnn.tutor.java.kafka.KafkaConfig;
import tungnn.tutor.java.kafka.KafkaUtil;

public class ProducerDemo {

  private static final String BOOTSTRAP_SERVER = "localhost:9092";
  private static final String TOPIC = "topic-demo";

  static void main(String[] args) {

    if (args.length < 2) {
      System.err.println("Usage: <key> <value>");
      return;
    }

    var key = args[0];
    var value = args[1];

    var props = KafkaConfig.producerProps(BOOTSTRAP_SERVER);

    try (var producer =
        KafkaUtil.newProducer(StringSerializer.class, StringSerializer.class, props)) {

      var record = new ProducerRecord<>(TOPIC, key, value);

      producer.send(
          record,
          (metadata, exception) -> {
            if (exception == null) {
              System.out.printf(
                  "Sent topic=%s partition=%d offset=%d%n",
                  metadata.topic(), metadata.partition(), metadata.offset());
            } else {
              System.err.println("Send failed: " + exception.getMessage());
            }
          });

      // Ensure message is actually sent before closing
      producer.flush();
    }
  }
}
