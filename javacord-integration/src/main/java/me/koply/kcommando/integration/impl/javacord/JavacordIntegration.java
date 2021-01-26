package me.koply.kcommando.integration.impl.javacord;

import me.koply.kcommando.CProcessParameters;
import me.koply.kcommando.Command;
import me.koply.kcommando.CommandHandler;
import me.koply.kcommando.Parameters;
import me.koply.kcommando.integration.Integration;
import me.koply.kcommando.plugin.PluginFile;
import me.koply.kcommando.plugin.PluginManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.GroupChannel;
import org.javacord.api.entity.message.MessageType;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.GloballyAttachableListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class JavacordIntegration extends Integration<MessageCreateEvent> {

    private final DiscordApi discordApi;

    public JavacordIntegration(final DiscordApi discordApi) {
        super(discordApi.getClientId());
        this.discordApi = discordApi;
    }

    @Override
    public void registerCommandHandler(CommandHandler<MessageCreateEvent> commandHandler) {
        discordApi.addMessageCreateListener(e ->
            commandHandler.processCommand(new CProcessParameters<>(new CProcessParameters.Author(e.getMessageAuthor()
                .getDiscriminatedName(),
                e.getMessageAuthor().getId(),
                e.getMessageAuthor().isBotUser()),
                e.getMessage().getType() == MessageType.NORMAL_WEBHOOK,
                e.getMessage().getContent(),
                channelName(e),
                e.getServer().isPresent() ? e.getServer().get().getId() : -1,
                e, e.getChannel().getId())));
    }

    private PluginManager<GloballyAttachableListener, JavacordCommand> pluginManager;

    @Override
    public void detectAndEnablePlugins(Parameters<MessageCreateEvent> params) {
        pluginManager = new PluginManager<>(params.getPluginsPath());
        pluginManager.detectPlugins();
        pluginManager.enablePlugins();
    }

    @Override
    public Set<Class<? extends Command>> getPluginCommands() {
        if (pluginManager == null) return null;

        Set<Class<? extends Command>> set = new HashSet<>();
        ArrayList<PluginFile<GloballyAttachableListener, JavacordCommand>> plugins = pluginManager.getPlugins();
        for (PluginFile<GloballyAttachableListener, JavacordCommand> plugin : plugins) {
            set.addAll(plugin.getInstance().getCommands());
        }
        return set;
    }

    @Override
    public void registerListeners() {
        if (pluginManager == null) return;

        ArrayList<PluginFile<GloballyAttachableListener, JavacordCommand>> plugins = pluginManager.getPlugins();
        for (PluginFile<GloballyAttachableListener, JavacordCommand> plugin : plugins) {
            for (GloballyAttachableListener listener : plugin.getInstance().getListeners()) {
                discordApi.addListener(listener);
            }
        }
    }

    private String channelName(final MessageCreateEvent event) {
        if (event.isPrivateMessage())
            return "(PRIVATE)";
        return event.getGroupChannel().map(GroupChannel::getName).map(Optional::get).orElse(event.getServer().get().getName());
    }
}