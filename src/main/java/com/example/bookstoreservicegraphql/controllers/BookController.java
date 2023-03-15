package com.example.bookstoreservicegraphql.controllers;

import com.example.bookstoreservicegraphql.data.models.Book;
import com.example.bookstoreservicegraphql.data.repositories.interfaces.IBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class BookController {

  @Autowired
  private IBookRepository bookRepository;

  @QueryMapping
  public Book findByAuthorsName(@Argument("authorName") String authorName) {
    return this.bookRepository.findByAuthorsName(authorName);
  }

  @QueryMapping
  public Book findByTitle(@Argument("title") String title) {
    return this.bookRepository.findByTitle(title);
  }
}
