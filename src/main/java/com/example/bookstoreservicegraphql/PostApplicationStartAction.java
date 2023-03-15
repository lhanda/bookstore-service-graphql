package com.example.bookstoreservicegraphql;

import com.example.bookstoreservicegraphql.data.models.Author;
import com.example.bookstoreservicegraphql.data.models.Book;
import com.example.bookstoreservicegraphql.data.repositories.interfaces.IAuthorRepository;
import com.example.bookstoreservicegraphql.data.repositories.interfaces.IBookRepository;
import java.sql.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PostApplicationStartAction implements CommandLineRunner {

  @Autowired
  private IBookRepository bookRepository;

  @Autowired
  private IAuthorRepository authorRepository;

  @Override
  public void run(String... args) throws Exception {
    this.initializeData();
  }

  private void initializeData() {
    Book book1 = Book
      .builder()
      .isbn("0000000001")
      .title("Book 1")
      .yearOfPublish(2001)
      .price(11)
      .genre("action")
      .build();
    this.bookRepository.save(book1);
    Author john = Author
      .builder()
      .name("John")
      .birthday(Date.valueOf("1991-01-01"))
      .build();
    john.setBook(book1);
    this.authorRepository.save(john);

    Book book2 = Book
      .builder()
      .isbn("0000000002")
      .title("Book 2")
      .yearOfPublish(2002)
      .price(12)
      .genre("fantasy")
      .build();
    this.bookRepository.save(book2);
    Author peter = Author
      .builder()
      .name("Peter")
      .birthday(Date.valueOf("1992-02-02"))
      .build();
    peter.setBook(book2);
    this.authorRepository.save(peter);

    Book book3 = Book
      .builder()
      .isbn("0000000003")
      .title("Book 3")
      .yearOfPublish(2003)
      .price(13)
      .genre("sci-fi")
      .build();
    this.bookRepository.save(book3);
    Author cindy = Author
      .builder()
      .name("Cindy")
      .birthday(Date.valueOf("1993-03-03"))
      .build();
    cindy.setBook(book3);
    this.authorRepository.save(cindy);
    Author steven = Author
      .builder()
      .name("Steven")
      .birthday(Date.valueOf("1994-04-04"))
      .build();
    steven.setBook(book3);
    this.authorRepository.save(steven);

    Book book4 = Book
      .builder()
      .isbn("0000000004")
      .title("Book 4")
      .yearOfPublish(2004)
      .price(14)
      .genre("romance")
      .build();
    this.bookRepository.save(book4);
    Author kelly = Author
      .builder()
      .name("Kelly")
      .birthday(Date.valueOf("1995-05-05"))
      .build();
    kelly.setBook(book4);
    this.authorRepository.save(kelly);

    Book book5 = Book
      .builder()
      .isbn("0000000005")
      .title("Book 5")
      .yearOfPublish(2005)
      .price(15)
      .genre("mystery")
      .build();
    this.bookRepository.save(book5);
    Author adam = Author
      .builder()
      .name("Adam")
      .birthday(Date.valueOf("1996-06-06"))
      .build();
    adam.setBook(book5);
    this.authorRepository.save(adam);
  }
}
