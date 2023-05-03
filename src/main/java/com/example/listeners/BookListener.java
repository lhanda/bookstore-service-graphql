package com.example.listeners;

import com.example.bookstoreservicegraphql.data.models.BookPayLoad;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BookListener {

  @KafkaListener(
    topics = "#{kafkaTopics.getTopics.get('book')}",
    groupId = "${spring.kafka.consumer.group-id}"
  )
  public void listen(@Payload BookPayLoad bookPayLoad) {
    log.info(
      "BookPayLoad received. BookId = {}, Book = {}, DataOperation={}",
      bookPayLoad.getBook().getIsbn(),
      bookPayLoad.getBook().getTitle(),
      bookPayLoad.getDataOperation().getDataOperationString()
    );
  }
}
