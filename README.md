# College Events Finder

## Running Locally

This will run with local authentication, skipping testing:

```
mvn clean spring-boot:run -P local -DskipTests
```

Test user credentials are set in `pom.xml`:
* Username: `student@uni.org`
* Password: `test`
