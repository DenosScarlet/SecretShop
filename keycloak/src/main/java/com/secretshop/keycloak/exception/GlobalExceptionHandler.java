package com.secretshop.keycloak.exception;

import com.nimbusds.oauth2.sdk.ErrorObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorObject> handleUserExists(UserAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorObject("USER_EXISTS", ex.getMessage()));
    }

    @ExceptionHandler(UserCreationException.class)
    public ResponseEntity<ErrorObject> handleCreationError(UserCreationException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorObject("CREATION_FAILED", ex.getMessage()));
    }
}
