package me.koply.kcommando.internal;

public interface ICommand<E> {

    // you should override just one
    default boolean handle(E event) { return true; } // 0x01
    default boolean handle(E event, String[] args) { return true; }// 0x02

}