package me.koply.kcommando.internal;

public interface ICommand<T> {

    // you should override just one
    default boolean handle(T e) { return true; } // 0x01
    default boolean handle(T e, String[] args) { return true; }// 0x02

}