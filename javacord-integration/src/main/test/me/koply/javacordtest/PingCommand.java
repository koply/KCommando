package me.koply.javacordtest;

import me.koply.kcommando.integration.impl.javacord.JavacordCommand;
import me.koply.kcommando.internal.Argument;
import me.koply.kcommando.internal.Commando;
import me.koply.kcommando.internal.KRunnable;
import org.javacord.api.event.message.MessageCreateEvent;

@Commando(name = "Ping",
    aliases = "ping")
public class PingCommand extends JavacordCommand {

    public PingCommand() {
        getInfo().setOnFalseCallback((KRunnable<MessageCreateEvent>) e -> e.getMessage().addReaction("⛔"));
    }

    @Override
    public boolean handle(MessageCreateEvent e, String[] args) {
        e.getChannel().sendMessage("Hello World!");
        return true;
    }

    @Argument(arg = "test")
    public boolean test(MessageCreateEvent e) {
        e.getChannel().sendMessage("Ben test");
        return true;
    }
}