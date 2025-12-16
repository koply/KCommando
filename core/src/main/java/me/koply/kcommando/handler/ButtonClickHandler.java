package me.koply.kcommando.handler;

import me.koply.kcommando.KCommando;
import me.koply.kcommando.internal.Kogger;
import me.koply.kcommando.internal.boxes.ButtonBox;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;

public class ButtonClickHandler {

    private final Map<String, ? extends ButtonBox> buttons;
    public ButtonClickHandler(final Map<String, ? extends ButtonBox> buttons) {
        this.buttons = Collections.unmodifiableMap(buttons);
    }

    public static class Parameters {
        public final Object event;
        public final String buttonId;

        public Parameters(final Object event, final String buttonId) {
            this.event = event;
            this.buttonId = buttonId;
        }
    }

    public void process(final Parameters par) {
        final ButtonBox box = this.buttons.getOrDefault(par.buttonId, null);
        if (box == null) return;

        try {
            box.method.invoke(box.instance, par.event);
        } catch (final InvocationTargetException | IllegalAccessException e) {
            if (KCommando.verbose) {
                Kogger.warn("An error occur while executing Button method. Stacktrace:");
                e.printStackTrace();
            }
        }

    }
}