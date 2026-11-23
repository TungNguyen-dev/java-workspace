package tungnn.tutor.java.kafka.demo;

import java.time.Duration;
import java.util.Collections;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import tungnn.tutor.java.kafka.KafkaConfig;
import tungnn.tutor.java.kafka.KafkaUtil;

public class ConsumerDemo {

  private static final String BOOTSTRAP_SERVER = "localhost:9092";
  private static final String GROUP_ID = "consumer-group-demo";
  private static final String TOPIC = "topic-demo";

  static void main(String[] args) {

    var props = KafkaConfig.consumerProps(BOOTSTRAP_SERVER, GROUP_ID);

    try (KafkaConsumer<String, String> consumer =
        KafkaUtil.newConsumer(StringDeserializer.class, StringDeserializer.class, props)) {

      // Subscribe ONCE
      consumer.subscribe(Collections.singletonList(TOPIC));

      while (true) {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));

        for (var record : records) {
          System.out.printf(
              "Consumed topic=%s partition=%d offset=%d key=%s value=%s%n",
              record.topic(), record.partition(), record.offset(), record.key(), record.value());

          if ("exit".equals(record.value())) {
            System.out.println("Shutdown consumer...");
            return; // graceful exit
          }
        }
      }
    }
  }
}
