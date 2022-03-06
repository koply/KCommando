package me.koply.kcommando.handler;

import me.koply.kcommando.internal.Kogger;
import me.koply.kcommando.internal.boxes.SlashBox;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class SlashCommandHandler {

    private final Map<String, SlashBox> commands;
    public SlashCommandHandler(Map<String, SlashBox> commands) {
        this.commands = commands;
    }

    public static class Parameters {
        public final Object event;
        public final String name;

        public Parameters(Object event, String name) {
            this.event = event;
            this.name = name;
        }
    }

    public void process(Parameters p) {
        SlashBox box = commands.get(p.name);
        if (box == null) return;

        try {
            box.method.invoke(box.instance, p.event);
        } catch (InvocationTargetException | IllegalAccessException e) {
            Kogger.warn("An error occured while handling a slash command. Stacktrace:");
            e.printStackTrace();
        }
    }
}