package com.mbienkowski.template.exception.user;

import com.mbienkowski.template.exception.ErrorResponseException;
import java.net.URI;
import java.time.Instant;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;

public class UserInactiveException extends ErrorResponseException {

    public UserInactiveException(String login) {
        super();
        var detail = "User with login '%s' is not active".formatted(login);
        problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), detail);
        problemDetail.setProperty("timestamp", Instant.now().toString());
        problemDetail.setType(URI.create("/errors/users/inactive"));
    }
}
