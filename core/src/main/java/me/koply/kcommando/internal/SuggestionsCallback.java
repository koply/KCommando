package me.koply.kcommando.internal;

import me.koply.kcommando.CommandInfo;

import java.util.Set;

public interface SuggestionsCallback {
    void run(Object e, Set<CommandInfo> similarCommands);
}