package com.example.bookstoreservicegraphql.configurations;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import java.sql.Date;
import java.time.format.DateTimeParseException;
import javax.validation.constraints.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class DateScalarConfiguration {

  @Bean
  public GraphQLScalarType dateScalar() {
    return GraphQLScalarType
      .newScalar()
      .name("Date")
      .description("Java Date")
      .coercing(
        new Coercing<Date, String>() {
          @Override
          public String serialize(@NotNull Object dataFetcherResult)
            throws CoercingSerializeException {
            if (dataFetcherResult instanceof Date) {
              return dataFetcherResult.toString();
            } else {
              throw new CoercingSerializeException("Expected a Date object!");
            }
          }

          @Override
          public @NotNull Date parseValue(@NotNull Object input)
            throws CoercingParseValueException {
            try {
              if (input instanceof String) {
                return Date.valueOf((String) input);
              } else {
                throw new CoercingParseValueException(
                  "Expected a String object!"
                );
              }
            } catch (DateTimeParseException dtpe) {
              throw new CoercingParseValueException(
                String.format(
                  "Not a valid date: '%s'! Exception: %s",
                  input,
                  dtpe.getMessage()
                )
              );
            }
          }

          @Override
          public @NotNull Date parseLiteral(@NotNull Object input)
            throws CoercingParseLiteralException {
            if (input instanceof StringValue) {
              try {
                return Date.valueOf(((StringValue) input).getValue());
              } catch (DateTimeParseException dtpe) {
                throw new CoercingParseLiteralException(
                  String.format(
                    "Not a valid date: '%s'! Exception: %s",
                    input,
                    dtpe.getMessage()
                  )
                );
              }
            } else {
              throw new CoercingParseLiteralException(
                "Expected a StringValue object!"
              );
            }
          }
        }
      )
      .build();
  }

  @Bean
  public RuntimeWiringConfigurer runtimeWiringConfigurer() {
    return builder -> builder.scalar(dateScalar()).build();
  }
}
