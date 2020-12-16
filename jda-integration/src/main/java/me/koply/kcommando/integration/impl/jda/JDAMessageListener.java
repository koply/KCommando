package me.koply.kcommando.integration.impl.jda;

import me.koply.kcommando.CProcessParameters;
import me.koply.kcommando.CommandHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class JDAMessageListener extends ListenerAdapter {

    private final CommandHandler commandHandler;

    public JDAMessageListener(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
        commandHandler.processCommand(new CProcessParameters(
                new CProcessParameters.Author(e.getAuthor().getName() +"#"+ e.getAuthor().getDiscriminator(),
                        e.getAuthor().getIdLong(),
                        e.getAuthor().isBot()),
                e.getMessage().isWebhookMessage(),
                e.getMessage().getContentRaw(),
                e.isFromGuild() ? e.getGuild().getName() : "(PRIVATE)",
                e.isFromGuild() ? e.getGuild().getIdLong() : -1,
                e, e.getChannel().getIdLong()));
    }
}