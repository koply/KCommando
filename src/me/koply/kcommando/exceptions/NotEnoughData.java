package me.koply.kcommando.exceptions;

public class NotEnoughData extends RuntimeException {
    public NotEnoughData(String message) {
        super(message);
    }
}