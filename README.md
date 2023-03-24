# bookstore-service-graphql
Demo project for creating GraphQL Api using Spring GraphQL, Spring Data & Spring JPA

To run:

1) Open command prompt at root folder, then run the command 'gradlew bootRun' without quotes
2) Navigate to http://localhost:8080/graphiql at your browser
3) Cut and paste the following query to your browser and click 'Play' button:

        query {
            findAllBooks {
                title
                yearOfPublish
                price
                genre
                isbn
                authors {
                    id
                    name
                    birthday
                    book {
                        title
                    }
                }
            }
            findByTitle(title: "Book 5") {
                title
                yearOfPublish
                price
                genre
                isbn
                authors {
                    id
                    name
                    birthday
                    book {
                        title
                    }
                }
            }
            findByAuthorsName(authorName: "Peter") {
                title
                yearOfPublish
                price
                genre
                isbn
                authors {
                    id
                    name
                    birthday
                    book {
                        title
                    }
                }
            }
            findAllAuthors {
                id
                name
                birthday
                book {
                    title
                }
            }
            findByBookTitle(bookTitle: "Book 5") {
                id
                name
                birthday
                book {
                    title
                }
            }
        }

4) Cut and paste the following mutation to your browser and click 'Play' button:

        mutation {
            createBook(
                input: {clientMutationId: "0000000006", title: "Book 6", yearOfPublish: 2023, price: 18.88, genre: "thriller"}
            ) {
                isbn
                title
            }
            createAuthor(
                input: {name: "Henry", birthday: "1988-08-08", bookIsbn: "0000000006"}
            ) {
                id
                name
                book {
                    isbn
                    title
                }
            }
            updateBook(
                input: {
                    clientMutationId: "0000000006", 
                    title: "Book 6 2nd Edition", 
                    yearOfPublish: 2024, 
                    price: 19.99, 
                    genre: "thriller"}
            ) {
                isbn
                title
                yearOfPublish
                price
                genre
            }
            deleteBook(input: {clientMutationId: "0000000006"})
            updateAuthor(
                input: {clientMutationId: "7", name: "Henry II", birthday: "1988-08-08", bookIsbn: "0000000005"}
            ) {
                id
                name
                book {
                    isbn
                    title
                }
            }
            deleteAuthor(input: {clientMutationId: "7"})
        }
5) Cut and paste the following mutation to your browser and click 'Play' button:


