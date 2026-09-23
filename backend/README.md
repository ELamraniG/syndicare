# Syndicare backend

Backend for the Syndicare project made with Spring Boot.

## run

Java 17 is required.

```bash
mvn spring-boot:run
```

The dev config uses H2. The api runs on port 8080.

The application does not create demo accounts or sample records. A new database starts empty; create the first account with `POST /api/auth/register`, then add buildings and the related data through the API.

Main parts of the backend:

- login/register with JWT
- buildings and apartments
- monthly charges and payments
- tickets
- announcements
- documents and uploads
- admin and owner dashboard

For production use the `prod` profile and set the database environment variables.

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```
