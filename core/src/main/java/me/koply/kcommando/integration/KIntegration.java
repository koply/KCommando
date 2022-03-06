package me.koply.kcommando.integration;

import me.koply.kcommando.handler.ButtonClickHandler;
import me.koply.kcommando.handler.CommandHandler;
import me.koply.kcommando.handler.SlashCommandHandler;
import me.koply.kcommando.internal.annotations.HandleSlash;

public interface KIntegration {

    void registerCommandHandler(CommandHandler handler);
    void registerSlashCommandHandler(SlashCommandHandler handler);
    void registerButtonClickHandler(ButtonClickHandler handler);

    void registerSlashCommand(HandleSlash commandInfo);

    Class<?> getMessageEventType();
    Class<?> getSlashEventType();
    Class<?> getButtonEventType();

}