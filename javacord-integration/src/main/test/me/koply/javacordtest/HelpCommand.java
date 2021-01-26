package me.koply.javacordtest;

import me.koply.kcommando.integration.impl.javacord.JavacordCommand;
import me.koply.kcommando.internal.annotations.Commando;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;

@Commando(name = "Help",
        aliases = "help",
        description = "This command")
public class HelpCommand extends JavacordCommand {

    @Override
    public boolean handle(MessageCreateEvent e) {
        e.getChannel().sendMessage(Test.getInstance().getHelpEmbed()
                .setColor(Color.BLUE)
                .setAuthor(e.getMessageAuthor())
                .setFooter(e.getApi().getYourself().getName(), e.getApi().getYourself().getAvatar().getUrl().toString()));
        return true;
    }
}