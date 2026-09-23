package com.example.auth.exception;

/**
 * Custom runtime exception thrown when a user attempts to register
 * with a username or email that is already taken in MongoDB.
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
