package com.example.bookstoreservicegraphql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.bookstoreservicegraphql.controllers.BookController;
import com.example.bookstoreservicegraphql.data.models.Author;
import com.example.bookstoreservicegraphql.data.models.Book;
import com.example.bookstoreservicegraphql.data.models.BookInput;
import com.example.bookstoreservicegraphql.data.models.BookPayLoad;
import com.example.bookstoreservicegraphql.data.models.DataOperation;
import com.example.bookstoreservicegraphql.data.repositories.interfaces.IAuthorRepository;
import com.example.bookstoreservicegraphql.data.repositories.interfaces.IBookRepository;
import java.util.ArrayList;
import java.util.Arrays;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

@Slf4j
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class BookControllerTest {

  private AutoCloseable autoCloseable;

  private BookController bookController;

  @Mock
  private IBookRepository bookRepository;

  @Mock
  private IAuthorRepository authorRepository;

  @BeforeEach
  public void setUp() {
    this.autoCloseable = MockitoAnnotations.openMocks(this);
    Sinks.Many<BookPayLoad> bookPayLoadSink = Sinks
      .many()
      .multicast()
      .onBackpressureBuffer(64, false);
    Flux<BookPayLoad> bookPayLoadFlux = bookPayLoadSink.asFlux();
    this.bookController = new BookController();
    this.bookController.setAuthorRepository(this.authorRepository);
    this.bookController.setBookRepository(this.bookRepository);
    this.bookController.setBookPayLoadFlux(bookPayLoadFlux);
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
  public void testFindAllBooks() {
    String book1Title = "title1";
    String book2Title = "title2";
    Book book1 = Book
      .builder()
      .isbn("isbn1")
      .title(book1Title)
      .yearOfPublish(2021)
      .price(9.99)
      .genre("genre1")
      .build();
    Book book2 = Book
      .builder()
      .isbn("isbn2")
      .title(book2Title)
      .yearOfPublish(2022)
      .price(10.99)
      .genre("genre2")
      .build();
    when(this.bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));
    Iterable<Book> books = this.bookController.findAllBooks();
    assertNotNull(books);
    ArrayList<Book> bookList = new ArrayList<>();
    books.iterator().forEachRemaining(bookList::add);
    assertEquals(2, bookList.size());
    assertEquals(book1Title, bookList.get(0).getTitle());
    assertEquals(book2Title, bookList.get(1).getTitle());
  }

  @Test
  public void testFindByAuthorsName() {
    Book book = Book
      .builder()
      .isbn("isbn1")
      .title("title1")
      .yearOfPublish(2021)
      .price(9.99)
      .genre("genre1")
      .build();
    when(this.bookRepository.findByAuthorsName("authorName")).thenReturn(book);
    Book result = this.bookController.findByAuthorsName("authorName");
    assertNotNull(result);
    assertEquals("title1", result.getTitle());
  }

  @Test
  public void testFindByTitle() {
    String bookTitle = "title1";
    Book book = Book
      .builder()
      .isbn("isbn1")
      .title(bookTitle)
      .yearOfPublish(2021)
      .price(9.99)
      .genre("genre1")
      .build();
    when(this.bookRepository.findByTitle(bookTitle)).thenReturn(book);
    Book result = this.bookController.findByTitle(bookTitle);
    assertNotNull(result);
    assertEquals("title1", result.getTitle());
  }

  @Test
  public void testCreateBook() {
    String isbn = "isbn1";
    String title = "title1";
    int yearOfPublish = 2021;
    double price = 9.99;
    String genre = "genre1";
    BookInput input = new BookInput();
    input.setClientMutationId(isbn);
    input.setTitle(title);
    input.setYearOfPublish(yearOfPublish);
    input.setPrice(price);
    input.setGenre(genre);
    Book expectedBook = Book
      .builder()
      .isbn(isbn)
      .title(title)
      .yearOfPublish(yearOfPublish)
      .price(price)
      .genre(genre)
      .build();
    when(this.bookRepository.save(any(Book.class))).thenReturn(expectedBook);
    Book actualBook = this.bookController.createBook(input);
    assertEquals(expectedBook, actualBook);
  }

  @Test
  public void testUpdateBook() {
    String isbn = "isbn1";
    String title = "title1";
    int yearOfPublish = 2021;
    double price = 9.99;
    String genre = "genre1";
    BookInput input = new BookInput();
    input.setClientMutationId(isbn);
    input.setTitle(title);
    input.setYearOfPublish(yearOfPublish);
    input.setPrice(price);
    input.setGenre(genre);
    Book originalBook = Book
      .builder()
      .isbn(isbn)
      .title("originalTitle")
      .yearOfPublish(2020)
      .price(8.99)
      .genre("originalGenre")
      .build();
    Book expectedBook = Book
      .builder()
      .isbn(isbn)
      .title(title)
      .yearOfPublish(yearOfPublish)
      .price(price)
      .genre(genre)
      .build();
    when(this.bookRepository.findById(isbn))
      .thenReturn(Optional.of(originalBook));
    when(this.bookRepository.save(any(Book.class))).thenReturn(expectedBook);
    Book actualBook = this.bookController.updateBook(input);
    assertEquals(expectedBook, actualBook);
  }

  @Test
  public void testDeleteBook() {
    String isbn = "isbn1";
    String title = "title1";
    int yearOfPublish = 2021;
    double price = 9.99;
    String genre = "genre1";
    BookInput input = new BookInput();
    input.setClientMutationId(isbn);
    input.setTitle(title);
    input.setYearOfPublish(yearOfPublish);
    input.setPrice(price);
    input.setGenre(genre);
    Book bookToBeDeleted = Book
      .builder()
      .isbn(isbn)
      .title(title)
      .yearOfPublish(yearOfPublish)
      .price(price)
      .genre(genre)
      .build();
    Author author = new Author();
    author.setBook(bookToBeDeleted);
    when(this.bookRepository.findById(isbn))
      .thenReturn(Optional.of(bookToBeDeleted));
    when(this.authorRepository.findByBookTitle(title)).thenReturn(author);
    boolean deleteSuccess = this.bookController.deleteBook(input);
    assertTrue(deleteSuccess);
  }

  @Test
  public void testNotifyBookChange() {
    BookPayLoad expectedBookPayLoad1 = BookPayLoad
      .builder()
      .book(new Book())
      .dataOperation(DataOperation.CREATED)
      .build();
    BookPayLoad expectedBookPayLoad2 = BookPayLoad
      .builder()
      .book(new Book())
      .dataOperation(DataOperation.UPDATED)
      .build();
    TestPublisher<BookPayLoad> testPublisher = TestPublisher.create();
    Flux<BookPayLoad> bookPayLoadFlux = testPublisher.flux();
    this.bookController.setBookPayLoadFlux(bookPayLoadFlux);
    Publisher<BookPayLoad> actualPublisher =
      this.bookController.notifyBookChange();
    assertEquals(bookPayLoadFlux, actualPublisher);
    StepVerifier
      .create(this.bookController.notifyBookChange())
      .then(() -> testPublisher.next(expectedBookPayLoad1))
      .assertNext(authorPayLoad -> authorPayLoad.equals(expectedBookPayLoad1))
      .then(() -> testPublisher.next(expectedBookPayLoad2))
      .assertNext(authorPayLoad -> authorPayLoad.equals(expectedBookPayLoad2))
      .then(testPublisher::complete)
      .expectComplete()
      .verify();
  }
}
