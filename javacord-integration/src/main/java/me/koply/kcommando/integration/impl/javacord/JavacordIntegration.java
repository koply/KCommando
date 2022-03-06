package me.koply.kcommando.integration.impl.javacord;

import me.koply.kcommando.handler.ButtonClickHandler;
import me.koply.kcommando.handler.CommandHandler;
import me.koply.kcommando.handler.SlashCommandHandler;
import me.koply.kcommando.integration.Integration;
import me.koply.kcommando.internal.annotations.HandleSlash;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;

public class JavacordIntegration extends Integration {

    protected JavacordIntegration(long selfId) {
        super(selfId);
    }

    @Override
    public void registerCommandHandler(CommandHandler handler) {

    }

    @Override
    public void registerSlashCommandHandler(SlashCommandHandler handler) {

    }

    @Override
    public void registerButtonClickHandler(ButtonClickHandler handler) {

    }

    @Override
    public void registerSlashCommand(HandleSlash commandInfo) {

    }

    @Override
    public Class<?> getMessageEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Class<?> getSlashEventType() {
        return SlashCommandCreateEvent.class;
    }

    @Override
    public Class<?> getButtonEventType() {
        return ButtonClickEvent.class;
    }
}