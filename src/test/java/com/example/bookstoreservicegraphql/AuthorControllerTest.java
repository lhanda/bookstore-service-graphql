package com.example.bookstoreservicegraphql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.bookstoreservicegraphql.configurations.KafkaTopics;
import com.example.bookstoreservicegraphql.controllers.AuthorController;
import com.example.bookstoreservicegraphql.data.models.Author;
import com.example.bookstoreservicegraphql.data.models.AuthorInput;
import com.example.bookstoreservicegraphql.data.models.AuthorPayLoad;
import com.example.bookstoreservicegraphql.data.models.Book;
import com.example.bookstoreservicegraphql.data.models.DataOperation;
import com.example.bookstoreservicegraphql.data.repositories.interfaces.IAuthorRepository;
import com.example.bookstoreservicegraphql.data.repositories.interfaces.IBookRepository;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;
import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

@Slf4j
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class AuthorControllerTest {

  private AutoCloseable autoCloseable;

  private AuthorController authorController;

  private TestPublisher<AuthorPayLoad> testPublisher;

  private Flux<AuthorPayLoad> authorPayLoadFlux;

  @Mock
  private IAuthorRepository authorRepository;

  @Mock
  private IBookRepository bookRepository;

  @Mock
  private KafkaTopics kafkaTopics;

  @Mock
  private KafkaTemplate<String, AuthorPayLoad> authorPayLoadKafkaTemplate;

  @BeforeEach
  public void setUp() {
    this.autoCloseable = MockitoAnnotations.openMocks(this);
    this.authorController = new AuthorController();
    this.authorController.setAuthorRepository(this.authorRepository);
    this.authorController.setBookRepository(this.bookRepository);
    this.authorController.setKafkaTopics(this.kafkaTopics);
    this.authorController.setAuthorPayLoadKafkaTemplate(
        this.authorPayLoadKafkaTemplate
      );
    this.testPublisher = TestPublisher.create();
    this.authorPayLoadFlux = this.testPublisher.flux();
    this.authorController.setAuthorPayLoadFlux(this.authorPayLoadFlux);
  }

  @AfterAll
  public void destroy() {
    try {
      this.autoCloseable.close();
    } catch (Exception e) {
      log.info("Cannot close autocloseable!", e);
    }
  }

  @Test
  public void testFindAllAuthors() {
    Iterable<Author> expectedAuthors = List.of(
      Author
        .builder()
        .id(1)
        .name("Alex")
        .birthday(Date.valueOf("1998-01-01"))
        .build()
    );
    when(this.authorRepository.findAll()).thenReturn(expectedAuthors);
    Iterable<Author> actualAuthors = this.authorController.findAllAuthors();
    assertEquals(expectedAuthors, actualAuthors);
  }

  @Test
  public void testFindByBookTitle() {
    String expectedBookTitle = "New Book";
    Author authorWithExpectedBook = new Author();
    authorWithExpectedBook.setBook(
      Book.builder().isbn("0000000001").title(expectedBookTitle).build()
    );
    when(this.authorRepository.findByBookTitle(expectedBookTitle))
      .thenReturn(authorWithExpectedBook);
    Author actualAuthor =
      this.authorController.findByBookTitle(expectedBookTitle);
    assertEquals(authorWithExpectedBook, actualAuthor);
  }

  @Test
  public void testCreateAuthor() {
    AuthorInput input = new AuthorInput();
    input.setClientMutationId("1");
    input.setName("New Author");
    input.setBirthday(Date.valueOf("2000-01-01"));
    input.setBookIsbn("1234567");

    Book expectedBook = Book.builder().isbn(input.getBookIsbn()).build();
    Author expectedAuthor = Author
      .builder()
      .id(Integer.parseInt(input.getClientMutationId()))
      .name(input.getName())
      .birthday(input.getBirthday())
      .book(expectedBook)
      .build();

    when(this.bookRepository.findById(input.getBookIsbn()))
      .thenReturn(Optional.of(expectedBook));
    when(this.authorRepository.save(any(Author.class)))
      .thenReturn(expectedAuthor);
    Author actualAuthor = this.authorController.createAuthor(input);
    assertEquals(expectedAuthor, actualAuthor);
  }

  @Test
  public void testUpdateAuthor() {
    AuthorInput input = new AuthorInput();
    input.setClientMutationId("1");
    input.setName("Updated Author");
    input.setBirthday(Date.valueOf("2000-01-01"));
    input.setBookIsbn("1234567");

    Book expectedBook = Book.builder().isbn(input.getBookIsbn()).build();
    Author originalAuthor = Author
      .builder()
      .id(1)
      .name("Original Author")
      .birthday(Date.valueOf("2000-01-01"))
      .book(Book.builder().isbn("0000000001").title("Original Book").build())
      .build();

    when(
      this.authorRepository.findById(
          Integer.parseInt(input.getClientMutationId())
        )
    )
      .thenReturn(Optional.of(originalAuthor));
    when(this.bookRepository.findById(input.getBookIsbn()))
      .thenReturn(Optional.of(expectedBook));

    Author expectedAuthor = Author
      .builder()
      .name("Updated Author")
      .birthday(Date.valueOf("2000-01-01"))
      .book(expectedBook)
      .build();
    when(this.authorRepository.save(any(Author.class)))
      .thenReturn(expectedAuthor);

    Author actualAuthor = this.authorController.updateAuthor(input);
    assertEquals(expectedAuthor, actualAuthor);
  }

  @Test
  public void testDeleteAuthor() {
    AuthorInput input = new AuthorInput();
    input.setClientMutationId("1");

    Author expectedAuthor = new Author();
    expectedAuthor.setId(1);

    when(
      this.authorRepository.findById(
          Integer.parseInt(input.getClientMutationId())
        )
    )
      .thenReturn(Optional.of(expectedAuthor));

    boolean deleteSuccess = this.authorController.deleteAuthor(input);

    assertTrue(deleteSuccess);
  }

  @Test
  public void testNotifyAuthorChange() {
    AuthorPayLoad expectedAuthorPayLoad1 = AuthorPayLoad
      .builder()
      .author(new Author())
      .dataOperation(DataOperation.CREATED)
      .build();
    AuthorPayLoad expectedAuthorPayLoad2 = AuthorPayLoad
      .builder()
      .author(new Author())
      .dataOperation(DataOperation.UPDATED)
      .build();
    Publisher<AuthorPayLoad> actualPublisher =
      this.authorController.notifyAuthorChange();
    assertEquals(this.authorPayLoadFlux, actualPublisher);
    StepVerifier
      .create(this.authorController.notifyAuthorChange())
      .then(() -> this.testPublisher.next(expectedAuthorPayLoad1))
      .assertNext(authorPayLoad -> authorPayLoad.equals(expectedAuthorPayLoad1))
      .then(() -> this.testPublisher.next(expectedAuthorPayLoad2))
      .assertNext(authorPayLoad -> authorPayLoad.equals(expectedAuthorPayLoad2))
      .then(this.testPublisher::complete)
      .expectComplete()
      .verify();
  }
}
