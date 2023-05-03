# bookstore-service-graphql
Demo project for creating GraphQL Api using Spring GraphQL, Spring Data, Spring JPA, Spring RSocket & Spring Kafka

To setup Kafka and Kafdrop:

1) Install Docker Desktop at https://www.docker.com/products/docker-desktop/
2) Open command prompt at root folder, then run the command 'docker-compose up -d'
3) Ensure the container 'bookstore-service-graphql' is started in docker desktop

To run:

1) Open command prompt at root folder, then run the command 'gradlew bootRun' without quotes
2) Navigate to http://localhost:8080/h2-console at your browser to access to h2 database with the following credentials:

        UserName: sa
        Password: password
3) Navigate to http://localhost:9000/ at your browser to access Kafdrop to explore messages in Kafka during Create, Update & Delete operations as follows in steps below 
4) Navigate to http://localhost:8080/graphiql at your browser to access in-browser GraphQL IDE
5) Copy and paste the following query to your browser and click 'Play' button:

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

6) Copy and paste the following mutation to your browser and click 'Play' button:

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
            deleteBook(input: {clientMutationId: "0000000006"})
            deleteAuthor(input: {clientMutationId: "7"})
        }
7) Copy and paste the following subscription to your browser and click 'Play' button:

        subscription {
            notifyBookChange {
                book {
                    title
                    yearOfPublish
                    price
                    genre
                    isbn
                }
                dataOperation
            }
        }

8) With the previous tab still running at Step 5, open a new tab on your browser, then copy and paste the following book mutations one by one and click 'Play' button: 

        mutation {
            createBook(
                input: {clientMutationId: "0000000006", title: "Book 6", yearOfPublish: 2023, price: 18.88, genre: "thriller"}
            ) {
                isbn
                title
            }
        }

        mutation {
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
        }

        mutation {
            deleteBook(input: {clientMutationId: "0000000006"})
        }

9) Copy and paste the following subscription to your browser and click 'Play' button:

        subscription {
            notifyAuthorChange {
                author {
                    id
                    name
                    book {
                        isbn
                        title
                    }
                }
                dataOperation
            }
        }

10) With the previous tab still running at Step 7, open a new tab on your browser, then copy and paste the following author mutations one by one and click 'Play' button:

        mutation {
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
        }

        mutation {
            updateAuthor(
                input: {clientMutationId: "8", name: "Henry II", birthday: "1988-08-08", bookIsbn: "0000000005"}
            ) {
                id
                name
                book {
                    isbn
                    title
                }
            }
        }

        mutation {
            deleteAuthor(input: {clientMutationId: "8"})
        }

11) To test rsocket requests, install RSocket Client CLI (rsc) from https://github.com/making/rsc

12) Open command prompt window, and run the following commands:

        rsc --request --route=graphql --dataMimeType="application/graphql+json" --data '{\"query\" : \"{findAllBooks{title,yearOfPublish,price,genre,isbn,authors{id,name,birthday,book{title}}}}\"}'  --debug tcp://localhost:7000/