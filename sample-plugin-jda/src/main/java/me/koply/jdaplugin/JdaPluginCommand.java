package me.koply.jdaplugin;

import me.koply.kcommando.integration.impl.jda.JDACommand;
import me.koply.kcommando.internal.annotations.Argument;
import me.koply.kcommando.internal.annotations.Commando;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Commando(name = "jdaplugin",
          aliases = "jdaplugin")
public class JdaPluginCommand extends JDACommand {

    @Override
    public boolean handle(MessageReceivedEvent e, String[] args, String prefix) {
        e.getChannel().sendMessage("handle").queue();
        return true;
    }

    @Argument(arg = "test")
    public boolean test(MessageReceivedEvent e) {
        e.getChannel().sendMessage("test argument").queue();
        return true;
    }
}