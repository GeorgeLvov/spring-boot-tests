# Spring Boot Testing
This repository is written to demonstrate the testing of a Spring Boot microservice.

All tests are divided into 3 following types:

- Unit
- Component (incl. `@WebMvcTest`, `@DataJpaTest`)
- Integration

Technologies that was used for the main functionality:
-
- Spring Boot 3 (Web and Webflux for `WebClient`)
- Spring Security Oauth2 (resource server and client server)
- Spring Retry
- PostgreSQL
- IBM MQ (JMS)

Technologies that were used by testing:
-
- Testcontainers
- Spring Cloud Contract WireMock
- Awaitility



