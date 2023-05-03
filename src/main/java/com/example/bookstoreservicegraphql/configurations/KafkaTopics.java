package com.example.bookstoreservicegraphql.configurations;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;

@Configuration
@ConfigurationProperties(prefix = "kafka")
@PropertySource("classpath:kafka-topics.properties")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KafkaTopics {

  @Getter
  @Setter
  private Map<String, String> topics = new HashMap<>();
}
