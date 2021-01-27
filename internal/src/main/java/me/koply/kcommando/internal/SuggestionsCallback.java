package me.koply.kcommando.internal;

import java.util.Set;

public interface SuggestionsCallback<T> {
    void run(T e, Set<CommandInfo<T>> similarCommands);
}