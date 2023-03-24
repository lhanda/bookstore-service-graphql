package com.example.bookstoreservicegraphql.data.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookInput {

  private String clientMutationId;

  private String title;

  private int yearOfPublish;

  private double price;

  private String genre;
}
