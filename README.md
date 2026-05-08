# ProgKorny

## Java maven / spring boot init + dependencies
https://start.spring.io/ : innen lehet initelni egy csomagoltat, de ez a része felmegy gitre, de nyomonkövetésre leírom ide is a kijelölendőket: 
- Maven/Java/Spring Boot 4.0.6
- Group: com.progkorny, Artifact: beadando, Package name: com.progkorny.beadando 
- Packaging: Jar / Configuration: Properties / Java: 25
- Dependencies: Spring Web, Thymeleaf, Spring Data JPA, H2 database, Validation, Spring Boot DevTools, PostgreSQL Driver.

## Docker POSTGRES
- docker-compose.yml fájl létrehozva
- docker compose up -d : konténerek futnak
- postgres18.2 hozzáadva, és Adminer is, ami 8080-as portra van kiengedve.
- Adminer: Server: Postgres, Username/Password/Database: .env-ben, de lokálisan az, amit docker-composeban megadunk.