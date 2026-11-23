package tungnn.tutor.java.kafka;

import java.util.Properties;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

public final class KafkaUtil {

  private KafkaUtil() {
    throw new UnsupportedOperationException("Utility class");
  }

  // ===== CONSUMER =====
  public static <K, V> KafkaConsumer<K, V> newConsumer(
      Class<? extends Deserializer<K>> keyDeserializer,
      Class<? extends Deserializer<V>> valueDeserializer,
      Properties baseProps) {

    var props = new Properties();
    props.putAll(baseProps);

    props.put("key.deserializer", keyDeserializer.getName());
    props.put("value.deserializer", valueDeserializer.getName());

    return new KafkaConsumer<>(props);
  }

  // ===== PRODUCER =====
  public static <K, V> KafkaProducer<K, V> newProducer(
      Class<? extends Serializer<K>> keySerializer,
      Class<? extends Serializer<V>> valueSerializer,
      Properties baseProps) {

    var props = new Properties();
    props.putAll(baseProps);

    props.put("key.serializer", keySerializer.getName());
    props.put("value.serializer", valueSerializer.getName());

    return new KafkaProducer<>(props);
  }
}
