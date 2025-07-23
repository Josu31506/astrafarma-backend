// src/main/java/com/example/astrafarma/exception/ResourceNotFoundException.java
package com.example.astrafarma.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource is not found.
 * Annotated with {@link ResponseStatus} so Spring translates it to
 * an HTTP 404 status instead of the default 500.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
