# SpringBooks API

A simple Spring Boot REST API for a community library (books & members) with borrow/return logic.

## Run

Build:
```
mvn clean package
```

Run:
```
mvn spring-boot:run
```

H2 Console: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:springbooksdb`)

Swagger UI: http://localhost:8080/docs

## Auth
Security is enabled. For development and tests the app accepts only a single Bearer token for now (a simple mock JwtDecoder is used). Example:

```
curl -H "Authorization: Bearer valid-token" http://localhost:8080/api/books
```

## Endpoints (examples)

- POST /api/books
- GET /api/books
- GET /api/books/{id}
- PUT /api/books/{id}
- DELETE /api/books/{id}

- POST /api/members
- GET /api/members
- GET /api/members/{id}
- PUT /api/members/{id}

- POST /api/books/borrow/{bookId}/member/{memberId}
- POST /api/books/return/{bookId}

Pagination: use `?page=0&size=10`

## Tests
Run:
```
mvn test
```

## Notes
- This project uses a simple JwtDecoder for local development. Replace it with a real decoder in production.
