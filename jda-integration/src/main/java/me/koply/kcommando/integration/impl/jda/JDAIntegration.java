package me.koply.kcommando.integration.impl.jda;

import me.koply.kcommando.internal.Command;
import me.koply.kcommando.CommandHandler;
import me.koply.kcommando.Parameters;
import me.koply.kcommando.integration.Integration;
import me.koply.kcommando.plugin.PluginFile;
import me.koply.kcommando.plugin.PluginManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class JDAIntegration extends Integration<MessageReceivedEvent> {

    private final JDA jda;

    public JDAIntegration(final JDA jda) {
        super(jda.getSelfUser().getIdLong());
        this.jda = jda;
    }

    @Override
    public void registerCommandHandler(CommandHandler<MessageReceivedEvent> commandHandler) {
        jda.addEventListener(new JDAMessageListener(commandHandler));
    }

    private PluginManager<ListenerAdapter> pluginManager;

    @Override
    public void detectAndEnablePlugins(Parameters<MessageReceivedEvent> params) {
        pluginManager = new PluginManager<>(params.getPluginsPath());
        pluginManager.detectPlugins();
        pluginManager.enablePlugins();
    }

    @Override
    public Set<Class<? extends Command>> getPluginCommands() {
        if (pluginManager == null) return null;

        Set<Class<? extends Command>> set = new HashSet<>();
        ArrayList<PluginFile<ListenerAdapter>> plugins = pluginManager.getPlugins();
        for (PluginFile<ListenerAdapter> plugin : plugins) {
            set.addAll(plugin.getInstance().getCommands());
        }
        return set;
    }

    @Override
    public void registerListeners() {
        if (pluginManager == null) return;

        ArrayList<PluginFile<ListenerAdapter>> plugins = pluginManager.getPlugins();
        for (PluginFile<ListenerAdapter> plugin : plugins) {
            for (ListenerAdapter listener : plugin.getInstance().getListeners()) {
                jda.addEventListener(listener);
            }
        }
    }

}