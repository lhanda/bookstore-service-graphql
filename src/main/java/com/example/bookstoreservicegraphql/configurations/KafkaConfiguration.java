package com.example.bookstoreservicegraphql.configurations;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.converter.JsonMessageConverter;

@Slf4j
@EnableKafka
@Configuration
@ComponentScan(basePackages = "com.example.listeners")
public class KafkaConfiguration {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Value("${spring.kafka.consumer.group-id}")
  private String groupId;

  @Value("${spring.kafka.consumer.auto-offset-reset}")
  private String autoOffsetReset;

  @Value("${spring.kafka.consumer.key-deserializer}")
  private String keyDeserializer;

  @Value("${spring.kafka.consumer.value-deserializer}")
  private String valueDeserializer;

  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.enable(
      DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT
    );
    objectMapper.enable(
      DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT
    );
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return objectMapper;
  }

  @Bean
  public Map<String, Object> consumerConfigs() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, this.groupId);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, this.autoOffsetReset);
    props.put(
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
      this.keyDeserializer
    );
    props.put(
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
      this.valueDeserializer
    );
    return props;
  }

  @Bean
  public ConsumerFactory<String, String> consumerFactory() {
    return new DefaultKafkaConsumerFactory<>(this.consumerConfigs());
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(this.consumerFactory());
    factory.setMessageConverter(new JsonMessageConverter(this.objectMapper()));
    factory.setRecordInterceptor(recordInterceptor -> {
      log.debug("Incoming message: {}", recordInterceptor.value());
      return recordInterceptor;
    });
    return factory;
  }
}
