package br.com.kafka.principal.configs;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@EnableKafka
@Configuration
public class ProducerConfig {

  @Bean
  public DefaultKafkaProducerFactory<String, String> producerFactory() {
    Map<String, Object> properties = new HashMap<>();
    properties.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    properties.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    properties.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    properties.put(org.apache.kafka.clients.producer.ProducerConfig.BATCH_SIZE_CONFIG, 16384);
    properties.put(org.apache.kafka.clients.producer.ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
    properties.put(org.apache.kafka.clients.producer.ProducerConfig.LINGER_MS_CONFIG, 10);

    return new DefaultKafkaProducerFactory<>(properties);
  }

  @Bean
  public KafkaTemplate<String, String> kafkaTemplate() {
    KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory());
    return kafkaTemplate;
  }

}
