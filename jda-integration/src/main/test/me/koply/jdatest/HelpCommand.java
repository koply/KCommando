package me.koply.jdatest;

import me.koply.kcommando.integration.impl.jda.JDACommand;
import me.koply.kcommando.internal.annotations.Commando;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;

@Commando(name = "Help",
          aliases = "help",
          description = "This command")
public class HelpCommand extends JDACommand {

    @Override
    public boolean handle(MessageReceivedEvent e) {
        e.getChannel().sendMessage(Test.getInstance().getHelpEmbed()
                .setColor(Color.BLUE)
                .setAuthor(e.getAuthor().getName(), null, e.getAuthor().getAvatarUrl())
                .setFooter(e.getJDA().getSelfUser().getName(), e.getJDA().getSelfUser().getAvatarUrl())
                .build()).queue();
        return true;
    }
}