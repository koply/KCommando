package me.koply.kcommando.internal;

import me.koply.kcommando.CommandInfo;

import java.util.HashSet;

public interface SuggestionsCallback<E> {
    void run(E e, HashSet<CommandInfo> similarCommands);
}