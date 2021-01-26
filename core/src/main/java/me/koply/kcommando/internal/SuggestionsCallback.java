package me.koply.kcommando.internal;

import me.koply.kcommando.CommandInfo;

import java.util.Set;

public interface SuggestionsCallback<T> {
    void run(T e, Set<CommandInfo<T>> similarCommands);
}