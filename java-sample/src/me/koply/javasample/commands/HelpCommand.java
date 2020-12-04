package me.koply.javasample.commands;

import me.koply.javasample.SampleBot;
import me.koply.kcommando.CommandUtils;
import me.koply.kcommando.annotations.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

@Command(names={"help","yardım"}, description = "Bu komut!")
public class HelpCommand implements CommandUtils {

    @Override
    public void handle(@NotNull MessageReceivedEvent e) {
        e.getTextChannel().sendMessage(SampleBot.COMMANDSEMBED
                .setAuthor(e.getAuthor().getName(), null, e.getAuthor().getAvatarUrl())
                .setFooter(e.getJDA().getSelfUser().getName(), e.getJDA().getSelfUser().getAvatarUrl())
                .setColor(randomColor())
                .build()).queue();
    }
}