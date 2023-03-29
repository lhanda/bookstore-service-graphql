package com.example.bookstoreservicegraphql.data.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DataOperation {
  CREATED("created"),
  UPDATED("updated"),
  DELETED("deleted");

  private String dataOperationString;
}
