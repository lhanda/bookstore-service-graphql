# bookstore-service-graphql
Demo project for creating GraphQL Api using Spring GraphQL, Spring Data & Spring JPA

To run:

1) Open command prompt at root folder, then run the command 'gradlew bootRun' without quotes
2) Navigate to http://localhost:8080/graphiql at your browser
3) Cut and paste the following query to your browser and click 'Play' button:

        query {
            findByTitle(title: "Book 5") {
                title
                year
                price
                genre
            }

            findByAuthorsName(authorName: "Peter") {
                title
                year
                price
                genre
            }
        }


