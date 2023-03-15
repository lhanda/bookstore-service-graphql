package com.example.bookstoreservicegraphql.data.repositories.interfaces;

import com.example.bookstoreservicegraphql.data.models.Author;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IAuthorRepository
  extends PagingAndSortingRepository<Author, Integer> {}
