package me.koply.javacordtest;

import me.koply.kcommando.integration.impl.javacord.JavacordCommand;
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
        if (args.length == 1) e.getChannel().sendMessage("Hello World!");
        else return false;
        return true;
    }
}