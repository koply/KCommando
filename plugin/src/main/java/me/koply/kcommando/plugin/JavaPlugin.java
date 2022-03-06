package me.koply.kcommando.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * @param <E> Listener like ListenerAdapter or GloballyAttachableListener
 */
public abstract class JavaPlugin<E> {
    protected final PluginInfo info;
    public final PluginInfo getInfo() { return info; }

    public final Logger getLogger()  { return info.getLogger(); }
    public final String getName() { return info.getName(); }
    public final File getDataFolder() { return info.getDataFolder(); }

    public JavaPlugin() {
        info = PluginCargo.getDelivery();
    }

    private final List<E> listeners = new ArrayList<>();
    public final List<E> getListeners() {
        return listeners;
    }
    @SafeVarargs
    public final void addListener(E...adapters) {
        listeners.addAll(Arrays.asList(adapters));
    }

    /*private final List<Class<? extends Command>> commands = new ArrayList<>();
    public final List<Class<? extends Command>> getCommands() {
        return commands;
    }
    @SafeVarargs
    public final void addCommand(Class<? extends Command>...coms) {
        commands.addAll(Arrays.asList(coms));
    }*/

    public abstract void onEnable();
    public abstract void onDisable();
}