package me.koply.javasample.commands;

import me.koply.javasample.SampleBot;
import me.koply.kcommando.Command;
import me.koply.kcommando.Utils;
import me.koply.kcommando.internal.Commando;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

@Commando(name = "Help",
        aliases={"help","yardım"},
        description = "Bu komut!")
public class HelpCommand extends Command {

    @Override
    public boolean handle(@NotNull MessageReceivedEvent e) {
        e.getTextChannel().sendMessage(SampleBot.COMMANDSEMBED
                .setAuthor(e.getAuthor().getName(), null, e.getAuthor().getAvatarUrl())
                .setFooter(e.getJDA().getSelfUser().getName(), e.getJDA().getSelfUser().getAvatarUrl())
                .setColor(Utils.randomColor())
                .build()).queue();
        return true;
    }
}