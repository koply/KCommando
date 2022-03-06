package me.koply.kcommando.handler;

import me.koply.kcommando.KCommando;
import me.koply.kcommando.internal.Kogger;
import me.koply.kcommando.internal.boxes.ButtonBox;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class ButtonClickHandler {

    private final Map<String, ButtonBox> buttons;
    public ButtonClickHandler(Map<String, ButtonBox> buttons) {
        this.buttons = buttons;
    }

    public static class Parameters {
        public final Object event;
        public final String buttonId;

        public Parameters(Object event, String buttonId) {
            this.event = event;
            this.buttonId = buttonId;
        }
    }

    public void process(Parameters par) {
        ButtonBox box = buttons.getOrDefault(par.buttonId, null);
        if (box == null) return;

        try {
            box.method.invoke(box.instance, par.event);
        } catch (InvocationTargetException | IllegalAccessException e) {
            if (KCommando.verbose) {
                Kogger.warn("An error occur while executing Button method. Stacktrace:");
                e.printStackTrace();
            }
        }

    }
}