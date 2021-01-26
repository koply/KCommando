package me.koply.kcommando.plugin;

import java.io.File;
import java.util.logging.Logger;

public class PluginInfo {
    private final File dataFolder;
    private final String name;
    private final Logger logger;

    public PluginInfo(File folder, String name, Logger logger) {
        dataFolder = folder;
        this.name = name;
        this.logger = logger;
    }

    public File getDataFolder() {
        return dataFolder;
    }
    public String getName() {
        return name;
    }
    public Logger getLogger() {
        return logger;
    }
}