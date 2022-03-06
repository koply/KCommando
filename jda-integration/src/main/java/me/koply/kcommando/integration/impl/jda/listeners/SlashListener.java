package me.koply.kcommando.integration.impl.jda.listeners;

import me.koply.kcommando.handler.SlashCommandHandler;
import me.koply.kcommando.internal.AsyncCaller;
import me.koply.kcommando.internal.Kogger;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashListener extends ListenerAdapter implements AsyncCaller {

    private final SlashCommandHandler handler;
    public SlashListener(SlashCommandHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        try {
            executorService.submit(() -> handler.process(new SlashCommandHandler.Parameters(event, event.getName())));
        } catch (Exception ex) {
            Kogger.warn("An error occured while processing a SlashCommandEvent. Stacktrace:");
            ex.printStackTrace();
        }
    }
}