package com.example.bookstoreservicegraphql.controllers;

import com.example.bookstoreservicegraphql.data.models.Author;
import com.example.bookstoreservicegraphql.data.models.AuthorInput;
import com.example.bookstoreservicegraphql.data.models.AuthorPayLoad;
import com.example.bookstoreservicegraphql.data.models.Book;
import com.example.bookstoreservicegraphql.data.models.DataOperation;
import com.example.bookstoreservicegraphql.data.repositories.interfaces.IAuthorRepository;
import com.example.bookstoreservicegraphql.data.repositories.interfaces.IBookRepository;
import java.util.Optional;
import lombok.Setter;
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
public class AuthorController {

  @Setter
  @Autowired
  private IAuthorRepository authorRepository;

  @Setter
  @Autowired
  private IBookRepository bookRepository;

  private Sinks.Many<AuthorPayLoad> authorPayLoadSink = Sinks
    .many()
    .multicast()
    .onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);

  @Setter
  private Flux<AuthorPayLoad> authorPayLoadFlux =
    this.authorPayLoadSink.asFlux();

  @QueryMapping
  public Iterable<Author> findAllAuthors() {
    return this.authorRepository.findAll();
  }

  @QueryMapping
  public Author findByBookTitle(@Argument String bookTitle) {
    return this.authorRepository.findByBookTitle(bookTitle);
  }

  @MutationMapping
  public Author createAuthor(@Argument AuthorInput input) {
    Author newAuthor = Author
      .builder()
      .name(input.getName())
      .birthday(input.getBirthday())
      .build();
    Optional<Book> bookFound =
      this.bookRepository.findById(input.getBookIsbn());
    if (bookFound.isPresent()) {
      newAuthor.setBook(bookFound.get());
    }
    Author savedAuthor = this.authorRepository.save(newAuthor);
    this.authorPayLoadSink.tryEmitNext(
        AuthorPayLoad
          .builder()
          .author(savedAuthor)
          .dataOperation(DataOperation.CREATED)
          .build()
      );
    return savedAuthor;
  }

  @MutationMapping
  public Author updateAuthor(@Argument AuthorInput input) {
    Optional<Author> authorFound =
      this.authorRepository.findById(
          Integer.parseInt(input.getClientMutationId())
        );
    if (authorFound.isPresent()) {
      Author originalAuthor = authorFound.get();
      originalAuthor.setName(input.getName());
      originalAuthor.setBirthday(input.getBirthday());
      Optional<Book> bookFound =
        this.bookRepository.findById(input.getBookIsbn());
      if (bookFound.isPresent()) {
        originalAuthor.setBook(bookFound.get());
      }
      Author savedAuthor = this.authorRepository.save(originalAuthor);
      this.authorPayLoadSink.tryEmitNext(
          AuthorPayLoad
            .builder()
            .author(savedAuthor)
            .dataOperation(DataOperation.UPDATED)
            .build()
        );
      return savedAuthor;
    } else {
      return null;
    }
  }

  @MutationMapping
  public boolean deleteAuthor(@Argument AuthorInput input) {
    boolean success = false;
    try {
      Optional<Author> authorFound =
        this.authorRepository.findById(
            Integer.parseInt(input.getClientMutationId())
          );
      if (authorFound.isPresent()) {
        Author authorToBeDeleted = authorFound.get();
        this.authorRepository.delete(authorToBeDeleted);
        this.authorPayLoadSink.tryEmitNext(
            AuthorPayLoad
              .builder()
              .author(authorToBeDeleted)
              .dataOperation(DataOperation.DELETED)
              .build()
          );
        success = true;
      } else {
        log.error("Author id '{}' not found!", input.getClientMutationId());
      }
    } catch (NumberFormatException nfe) {
      log.error(
        "Cannot covert id {} to integer!",
        input.getClientMutationId(),
        nfe.getLocalizedMessage()
      );
    } catch (IllegalArgumentException iae) {
      log.error("Id is null!", iae.getLocalizedMessage());
    }

    return success;
  }

  @SubscriptionMapping
  public Publisher<AuthorPayLoad> notifyAuthorChange() {
    return this.authorPayLoadFlux;
  }
}
