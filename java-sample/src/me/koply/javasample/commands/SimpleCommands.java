package me.koply.javasample.commands;

import me.koply.kcommando.Command;
import me.koply.kcommando.Utils;
import me.koply.kcommando.internal.Commander;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Commander(name = "Ping",
        aliases = {"ping","pingu"},
        description = "Pong!")
public class SimpleCommands extends Command {

    @Override
    public boolean handle(MessageReceivedEvent e) {
        e.getTextChannel().sendMessage(Utils.embed("Pong!")).queue();
        return true;
    }

}