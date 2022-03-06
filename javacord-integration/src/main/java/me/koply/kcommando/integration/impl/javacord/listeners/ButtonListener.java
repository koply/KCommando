package me.koply.kcommando.integration.impl.javacord.listeners;

import me.koply.kcommando.KCommando;
import me.koply.kcommando.handler.ButtonClickHandler;
import me.koply.kcommando.internal.AsyncCaller;
import me.koply.kcommando.internal.Kogger;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.listener.interaction.ButtonClickListener;

public class ButtonListener implements ButtonClickListener, AsyncCaller {

    private final ButtonClickHandler handler;
    public ButtonListener(ButtonClickHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        ButtonClickHandler.Parameters par = new ButtonClickHandler.Parameters(event, event.getButtonInteraction().getCustomId());

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