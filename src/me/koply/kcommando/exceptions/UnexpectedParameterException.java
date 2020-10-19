package me.koply.kcommando.exceptions;

public class UnexpectedParameterException extends RuntimeException {
    public UnexpectedParameterException(String message) {
        super(message);
    }
}