package com.mbienkowski.template.exception;

import lombok.Getter;
import org.springframework.http.ProblemDetail;

@Getter
public abstract class ErrorResponseException extends RuntimeException {

    protected ProblemDetail problemDetail;

}
