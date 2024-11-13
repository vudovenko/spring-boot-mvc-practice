package dev.vudovenko.springbootmvcpractice.exceptionHandling.exceptions;

public class PetNotFoundException extends RuntimeException {

    public PetNotFoundException(String message) {
        super(message);
    }
}
