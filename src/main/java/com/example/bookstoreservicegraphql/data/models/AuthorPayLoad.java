package com.example.bookstoreservicegraphql.data.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AuthorPayLoad {

  private Author author;
  private DataOperation dataOperation;
}
