package com.example.bookstoreservicegraphql.controllers;

import com.example.bookstoreservicegraphql.data.models.Author;
import com.example.bookstoreservicegraphql.data.models.Book;
import com.example.bookstoreservicegraphql.data.models.BookInput;
import com.example.bookstoreservicegraphql.data.repositories.interfaces.IAuthorRepository;
import com.example.bookstoreservicegraphql.data.repositories.interfaces.IBookRepository;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class BookController {

  @Autowired
  private IBookRepository bookRepository;

  @Autowired
  private IAuthorRepository authorRepository;

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
    return this.bookRepository.save(newBook);
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
      return this.bookRepository.save(originalBook);
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
        Author author =
          this.authorRepository.findByBookTitle(bookFound.get().getTitle());
        author.setBook(null);
        this.authorRepository.save(author);
        this.bookRepository.deleteById(input.getClientMutationId());
        success = true;
      } else {
        log.error("Book id '{}' not found!", input.getClientMutationId());
      }
    } catch (IllegalArgumentException iae) {
      log.error("Id is null!", iae.getLocalizedMessage());
    }
    return success;
  }
}
