package me.koply.kcommando.internal;

public interface KRunnable {
    <E> void run(E event);
}