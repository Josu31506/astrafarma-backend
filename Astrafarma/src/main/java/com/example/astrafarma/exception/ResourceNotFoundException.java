// src/main/java/com/example/astrafarma/exception/ResourceNotFoundException.java
package com.example.astrafarma.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
