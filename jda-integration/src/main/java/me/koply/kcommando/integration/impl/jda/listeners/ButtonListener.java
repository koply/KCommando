package me.koply.kcommando.integration.impl.jda.listeners;

import me.koply.kcommando.KCommando;
import me.koply.kcommando.handler.ButtonClickHandler;
import me.koply.kcommando.internal.AsyncCaller;
import me.koply.kcommando.internal.Kogger;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ButtonListener extends ListenerAdapter implements AsyncCaller {

    private final ButtonClickHandler handler;
    public ButtonListener(ButtonClickHandler handler) {
        this.handler = handler;
    }

    /*
     * TODO: enhance this method
     */
    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        if (event.getButton() == null) return;
        ButtonClickHandler.Parameters par = new ButtonClickHandler.Parameters(event, event.getButton().getId());

        try {
            executorService.submit(() -> handler.process(par));
        } catch (Exception ex) {
            if (KCommando.verbose) {
                Kogger.warn("An error occur while processing ButtonClickEvent. Stacktrace:");
                ex.printStackTrace();
            }
        }
    }
}