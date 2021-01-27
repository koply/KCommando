package me.koply.jcordplugin;

import me.koply.kcommando.integration.impl.javacord.JavacordCommand;
import me.koply.kcommando.internal.annotations.Argument;
import me.koply.kcommando.internal.annotations.Commando;
import org.javacord.api.event.message.MessageCreateEvent;

@Commando(name = "jcordcommand",
          aliases = "jcordcommand")
public class JcordPluginCommand extends JavacordCommand {

    @Override
    public boolean handle(MessageCreateEvent e, String[] args, String prefix) {
        e.getChannel().sendMessage("handle");
        return true;
    }

    @Argument(arg = "test")
    public boolean test(MessageCreateEvent e) {
        e.getChannel().sendMessage("test argument");
        return true;
    }
}