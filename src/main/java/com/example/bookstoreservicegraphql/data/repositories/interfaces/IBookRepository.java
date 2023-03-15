package com.example.bookstoreservicegraphql.data.repositories.interfaces;

import com.example.bookstoreservicegraphql.data.models.Book;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface IBookRepository
  extends PagingAndSortingRepository<Book, String> {
  Book findByTitle(@Param("title") String title);

  Book findByAuthorsName(@Param("authorName") String authorName);
}
