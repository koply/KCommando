package me.koply.kcommando.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * @param <E> Listener like ListenerAdapter or GloballyAttachableListener
 * @param <T> Command like JDACommand or JavacordCommand
 */
public abstract class JavaPlugin<E, T> {
    protected final PluginInfo info;
    public final PluginInfo getInfo() { return info; }

    public final Logger getLogger()  { return info.getLogger(); }
    public final String getName() { return info.getName(); }
    public final File getDataFolder() { return info.getDataFolder(); }

    public JavaPlugin() {
        info = PluginCargo.getDelivery();
    }

    private final ArrayList<E> listeners = new ArrayList<>();
    public final ArrayList<E> getListeners() {
        return listeners;
    }
    public final void addListener(E...adapters) {
        listeners.addAll(Arrays.asList(adapters));
    }

    private final ArrayList<Class<T>> commands = new ArrayList<>();
    public final ArrayList<Class<T>> getCommands() {
        return commands;
    }
    public final void addCommand(Class<T>...coms) {
        commands.addAll(Arrays.asList(coms));
    }

    public abstract void onEnable();
    public abstract void onDisable();
}