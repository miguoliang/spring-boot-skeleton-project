package com.muchencute.biz.keycloak.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {

  public ConflictException() {
    super("HTTP Conflict 409");
  }

  public ConflictException(String message) {
    super(message);
  }
}
