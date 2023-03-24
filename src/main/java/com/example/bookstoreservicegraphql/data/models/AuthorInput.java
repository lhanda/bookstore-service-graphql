package com.example.bookstoreservicegraphql.data.models;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorInput {

  private String clientMutationId;

  private String name;

  private Date birthday;

  private String bookIsbn;
}
