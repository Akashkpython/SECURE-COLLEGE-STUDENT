package com.nmamit.service.exception;

/**
 * Thrown when login credentials are invalid (wrong email or password).
 * Results in a 401 Unauthorized response.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
