package com.example.bookstoreservicegraphql.listeners;

import com.example.bookstoreservicegraphql.data.models.AuthorPayLoad;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthorListener {

  @KafkaListener(
    topics = "#{kafkaTopics.getTopics.get('author')}",
    groupId = "${spring.kafka.consumer.group-id}"
  )
  public void listen(@Payload AuthorPayLoad authorPayLoad) {
    log.info(
      "AuthorPayLoad received. AuthorId = {}, Author = {}, DataOperation={}",
      authorPayLoad.getAuthor().getId(),
      authorPayLoad.getAuthor().getName(),
      authorPayLoad.getDataOperation().getDataOperationString()
    );
  }
}
