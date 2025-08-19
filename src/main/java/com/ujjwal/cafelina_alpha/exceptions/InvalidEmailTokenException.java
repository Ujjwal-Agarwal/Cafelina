package com.ujjwal.cafelina_alpha.exceptions;

public class InvalidEmailTokenException extends RuntimeException {
    public InvalidEmailTokenException(String message) {
        super(message);
    }
}
