package me.koply.kcommando.integration.impl.javacord.listeners;

import me.koply.kcommando.handler.SlashCommandHandler;
import me.koply.kcommando.internal.AsyncCaller;
import me.koply.kcommando.internal.Kogger;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

public class SlashListener implements SlashCommandCreateListener, AsyncCaller {

    private final SlashCommandHandler handler;
    public SlashListener(SlashCommandHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        try {
            executorService.submit(() -> handler.process(new SlashCommandHandler.Parameters(event, event.getSlashCommandInteraction().getCommandName())));
        } catch (Exception ex) {
            Kogger.warn("An error occured while processing a SlashCommandCreateEvent. Stacktrace:");
            ex.printStackTrace();
        }
    }
}