package me.koply.kcommando.integration;

import me.koply.kcommando.CommandHandler;

public interface Integration {
    void register(CommandHandler commandHandler);
}