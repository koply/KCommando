package me.koply.kcommando.integration.impl.javacord;

import me.koply.kcommando.CProcessParameters;
import me.koply.kcommando.CommandHandler;
import me.koply.kcommando.integration.Integration;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.GroupChannel;
import org.javacord.api.entity.message.MessageType;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.Optional;

public class JavacordIntegration implements Integration {

    private final DiscordApi discordApi;

    public JavacordIntegration(final DiscordApi discordApi) {
        this.discordApi = discordApi;
    }

    @Override
    public void register(CommandHandler commandHandler) {
        discordApi.addMessageCreateListener(event ->
            commandHandler.processCommand(new CProcessParameters(new CProcessParameters.Author(event.getMessageAuthor()
                .getDiscriminatedName(),
                event.getMessageAuthor().getId(),
                event.getMessageAuthor().isBotUser()),
                event.getMessage().getType() == MessageType.NORMAL_WEBHOOK,
                event.getMessage().getContent(),
                channelName(event),
                event.isServerMessage(),
                event)));
    }

    private String channelName(final MessageCreateEvent event) {
        if (event.isPrivateMessage())
            return "(PRIVATE)";
        return event.getGroupChannel().map(GroupChannel::getName).map(Optional::get).orElse(event.getServerTextChannel().get().getName());
    }
}