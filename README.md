
# Template &rarr; Java & Spring Boot &bull; MongoDB

This is a template application with multiple things preconfigured to be used right of the bat.

### 🧠 MongoDB
- ✅ Supporting transactions (full ACID support)
- ✅ Supporting migrations using Mongock.
  - Click here to see the [example migration class](./src/main/java/com/mbienkowski/template/db/migrations/V20230101153000_UserAddFieldActive.java). 
  - Transactions are turned on for all migrations by default.
  - For migration please use `mongoTemplate` as much as you can, as this is the fastest method to perform the data changes.
- ✅ Supporting encryption for specific data:
  - The data is encrypted on serialization & decrypted on deserialization.
  - Therefore, the encryption in both for data-in-transit & data-at-rest.
- 🔥 Before going live change the default 256-bit AES Key in properties file to a random one: `spring.data.mongodb.encryption.key`.

### 🔑 Security
- ✅ Spring Security is supporting Users with roles assignment:
  - In order to restrict an endpoint to specific role, annotate the method with `@HasRole("ROLE_NAME")`.
  - When there is no annotation, the endpoints are still protected by JWT Token authorisation, but any role can access it.
  - All requests that are not authorized are being logged.
- ✅ Users can authenticate by using the REST API published on `/api/v1/users/authenticate` endpoint:
  - The request requires `login`, `password`, and `device` object.
  - On the authentication response user receives `JWT Token` that contain enriched with `deviceSecurityToken` assigned to a specific user device.
  - The `deviceSecurityToken` gives us the ability to logout/cut-off specific devices, which would be impossible with a simple JWT Token.
- 🔥 Before going live change the default 256-bit JWT Encryption Key in properties file to a random one: `spring.data.mongodb.encryption.key`.

### 🧯 Exception handling
- ✅ Returning RFC 7807 compliant responses of type `application/problem+json`.
- ✅ Generic way of handling exceptions:
  - In order to use the generic way of handling the exception throw exceptions that are extending [`ErrorResponseException`](./src/main/java/com/mbienkowski/template/exception/ErrorResponseException.java).
  - Each exception is required to assign the `ProblemDetail`.
  - Look into already existing [`UserInactiveException`](./src/main/java/com/mbienkowski/template/exception/user/UserInactiveException.java) for more details.

### 🕹 JUnit Tests
- ✅ All tests are running in parallel (both classes & tests).
- ✅ Setup Integration Tests by extending the [`BaseIntegrationTest`](./src/test/java/com/mbienkowski/template/BaseIntegrationTest.java) class.

### 🗞 Logging
- ✅ Logs are easy to read, you can also configure the format to your needs, look into [HttpServletLoggingFormatter](./src/main/java/com/mbienkowski/template/logging/servlet/HttpServletLoggingFormatter.java).
- ✅ Supporting logging to `Console`, `File`.
  - Console appender in enabled everywhere where profile is not set to `staging` nor `production`.
  - File appender is always enabled.

### 🛠️ First run
- ✅ On your first application startup the Users of type Admin & Client are created automatically:
  - Random passwords are created for the users
  - You can find the password of specific user in the application logs.

# Development setup
### Requirements
1. Install JDK 19
2. Run local MongoDB instance `docker compose -f docker-compose.db.yml up -d`
3. Run `./gradlew bootRun`
4. Success! Now you can request the application with your favourite REST API client. 

# Creating new application with the template

Yes, this is a manual process, brace yourself for search-and-replace work.

### Steps to take when copying the template for new application

1. Copy the project to a new directory.
2. Enter the new application directory
3. Run `rm -rf .git` to remove the template `git` repository.
4. Look for `template`/`mbienkowski` keywords when renaming application items.
5. Remove not needed sections in readme (like this one).
6. Run `./gradlew clean build` when you think that everything is working. 
7. Setup new git repo, commit & push the changes to origin.

# Example Requests

## User Authentication
```
curl --location --request POST 'localhost:8080/api/v1/user/authenticate' \
--header 'Content-Type: application/json' \
--data-raw '{
    "login": "<user-login>",
    "password": "<user-password>",
    "device" : {
        "id": "<device-id>",
        "fingerprint": "<device-fingerprint>",
        "systemName": "iOS",
        "systemVersion": "13.0"
    }
}'
```
Will return JWT Token for further communication.

## Dummy Endpoint

### Endpoint available for role: `Client`
```
curl --location --request GET 'localhost:8080/api/v1/user/dummy' \
--header 'Content-Type: application/json' \
--header 'Authentication: <jwtToken>'
```
Will return: `dummy-client-response`.

### Endpoint available for role: `Admin`
```
curl --location --request GET 'localhost:8080/api/v1/admin/dummy' \
--header 'Content-Type: application/json' \
--header 'Authentication: <jwtToken>'
```
Will return: `dummy-admin-response`.
