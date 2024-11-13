package dev.vudovenko.springbootmvcpractice.exceptionHandling.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
