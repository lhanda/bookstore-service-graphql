package com.example.bookstoreservicegraphql.controllers;

import com.example.bookstoreservicegraphql.data.models.Author;
import com.example.bookstoreservicegraphql.data.models.Book;
import com.example.bookstoreservicegraphql.data.models.BookInput;
import com.example.bookstoreservicegraphql.data.models.BookPayLoad;
import com.example.bookstoreservicegraphql.data.models.DataOperation;
import com.example.bookstoreservicegraphql.data.repositories.interfaces.IAuthorRepository;
import com.example.bookstoreservicegraphql.data.repositories.interfaces.IBookRepository;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

@Slf4j
@Controller
public class BookController {

  @Autowired
  private IBookRepository bookRepository;

  @Autowired
  private IAuthorRepository authorRepository;

  private Sinks.Many<BookPayLoad> bookPayLoadSink = Sinks
    .many()
    .multicast()
    .onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);

  private Flux<BookPayLoad> bookPayLoadFlux = this.bookPayLoadSink.asFlux();

  @QueryMapping
  public Iterable<Book> findAllBooks() {
    return this.bookRepository.findAll();
  }

  @QueryMapping
  public Book findByAuthorsName(@Argument("authorName") String authorName) {
    return this.bookRepository.findByAuthorsName(authorName);
  }

  @QueryMapping
  public Book findByTitle(@Argument("title") String title) {
    return this.bookRepository.findByTitle(title);
  }

  @MutationMapping
  public Book createBook(@Argument BookInput input) {
    Book newBook = Book
      .builder()
      .isbn(input.getClientMutationId())
      .title(input.getTitle())
      .yearOfPublish(input.getYearOfPublish())
      .price(input.getPrice())
      .genre(input.getGenre())
      .build();
    this.bookRepository.save(newBook);
    this.bookPayLoadSink.tryEmitNext(
        BookPayLoad
          .builder()
          .book(newBook)
          .dataOperation(DataOperation.CREATED)
          .build()
      );
    return newBook;
  }

  @MutationMapping
  public Book updateBook(@Argument BookInput input) {
    Optional<Book> bookFound =
      this.bookRepository.findById(input.getClientMutationId());
    if (bookFound.isPresent()) {
      Book originalBook = bookFound.get();
      originalBook.setTitle(input.getTitle());
      originalBook.setYearOfPublish(input.getYearOfPublish());
      originalBook.setPrice(input.getPrice());
      originalBook.setGenre(input.getGenre());
      this.bookRepository.save(originalBook);
      this.bookPayLoadSink.tryEmitNext(
          BookPayLoad
            .builder()
            .book(originalBook)
            .dataOperation(DataOperation.UPDATED)
            .build()
        );
      return originalBook;
    } else {
      return null;
    }
  }

  @MutationMapping
  public boolean deleteBook(@Argument BookInput input) {
    boolean success = false;
    try {
      Optional<Book> bookFound =
        this.bookRepository.findById(input.getClientMutationId());
      if (bookFound.isPresent()) {
        Book bookToBeDeleted = bookFound.get();
        Author author =
          this.authorRepository.findByBookTitle(bookToBeDeleted.getTitle());
        if (author != null) {
          author.setBook(null);
          this.authorRepository.save(author);
        }
        this.bookRepository.delete(bookToBeDeleted);
        this.bookPayLoadSink.tryEmitNext(
            BookPayLoad
              .builder()
              .book(bookToBeDeleted)
              .dataOperation(DataOperation.DELETED)
              .build()
          );
        success = true;
      } else {
        log.error("Book id '{}' not found!", input.getClientMutationId());
      }
    } catch (IllegalArgumentException iae) {
      log.error("Id is null!", iae.getLocalizedMessage());
    }
    return success;
  }

  @SubscriptionMapping
  public Publisher<BookPayLoad> notifyBookChange() {
    return this.bookPayLoadFlux;
  }
}
