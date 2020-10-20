package me.koply.javasample.commands;

import me.koply.kcommando.CommandUtils;
import me.koply.kcommando.annotations.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SimpleCommands implements CommandUtils {

    @Command(names = {"ping","pingu"},
            description = "Pong!")
    public void pingCommand(MessageReceivedEvent e) {
        e.getTextChannel().sendMessage(embed("Pong!")).queue();
    }

}