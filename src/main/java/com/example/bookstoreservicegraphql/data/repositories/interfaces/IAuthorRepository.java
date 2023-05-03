package com.example.bookstoreservicegraphql.data.repositories.interfaces;

import com.example.bookstoreservicegraphql.data.models.Author;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface IAuthorRepository
  extends PagingAndSortingRepository<Author, Integer> {
  Author findByBookTitle(@Param("bookTitle") String bookTitle);
}
