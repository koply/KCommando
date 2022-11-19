package me.koply.kcommando.integration;

import me.koply.kcommando.handler.ButtonClickHandler;
import me.koply.kcommando.handler.CommandHandler;
import me.koply.kcommando.handler.SlashCommandHandler;
import me.koply.kcommando.internal.boxes.SlashBox;

public interface KIntegration {

    void registerCommandHandler(CommandHandler handler);
    void registerSlashCommandHandler(SlashCommandHandler handler);
    void registerButtonClickHandler(ButtonClickHandler handler);

    void registerSlashCommand(SlashBox commandInfo);

    Class<?> getMessageEventType();
    Class<?> getSlashEventType();
    Class<?> getButtonEventType();

}