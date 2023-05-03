package com.example.bookstoreservicegraphql.data.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(
  generator = ObjectIdGenerators.PropertyGenerator.class,
  property = "isbn"
)
public class Book {

  @Id
  private String isbn;

  @Column
  private String title;

  @OneToMany(
    fetch = FetchType.EAGER,
    mappedBy = "book",
    cascade = CascadeType.ALL
  )
  private List<Author> authors;

  @Column
  private int yearOfPublish;

  @Column
  private double price;

  @Column
  private String genre;
}
