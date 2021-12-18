package me.koply.kcommando;

import me.koply.kcommando.integration.Integration;

import java.io.File;
import java.util.*;

@SuppressWarnings("ALL")
public final class Parameters<T> {
    private Integration<T> integration;

    private String packagePath, prefix;

    private long cooldown = 0L;

    private Map<String, String> groupLocales;

    private final Set<String> owners = new HashSet<>();

    private Map<String, CommandToRun<T>> commandMethods;

    private boolean readBotMessages;

    private boolean useCaseSensitivity = false;
    private DataManager<T> dataManager;

    private File pluginsPath;

    public Integration<T> getIntegration() {
        return integration;
    }

    public Parameters<T> setIntegration(Integration<T> integration) {
        this.integration = integration;
        return this;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public Parameters<T> setPackagePath(String packagePath) {
        this.packagePath = packagePath;
        return this;
    }

    public String getPrefix() {
        return prefix;
    }

    public Parameters<T> setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public long getCooldown() {
        return cooldown;
    }

    public Parameters<T> setCooldown(long cooldown) {
        this.cooldown = cooldown;
        return this;
    }

    public Map<String, String> getGroupLocales() {
        return groupLocales;
    }

    public Parameters<T> setGroupLocales(Map<String, String> groupLocales) {
        this.groupLocales = groupLocales;
        return this;
    }

    public Parameters<T> setOwners(String...owners) {
        this.owners.addAll(Arrays.asList(owners));
        return this;
    }

    public Set<String> getOwners() {
        return owners;
    }

    public Map<String, CommandToRun<T>> getCommandMethods() {
        return commandMethods;
    }

    public Parameters<T> setCommandMethods(Map<String, CommandToRun<T>> commandMethods) {
        this.commandMethods = commandMethods;
        return this;
    }

    public boolean isReadBotMessages() {
        return readBotMessages;
    }

    public Parameters<T> setReadBotMessages(boolean readBotMessages) {
        this.readBotMessages = readBotMessages;
        return this;
    }

    public boolean isCaseSensitivity() {
        return useCaseSensitivity;
    }

    public Parameters<T> useCaseSensitivity() {
        this.useCaseSensitivity = true;
        return this;
    }

    public Optional<DataManager<T>> getDataManager() {
        return Optional.of(dataManager);
    }

    public Parameters<T> setDataFile(File dataFile) {
        this.dataManager = new DataManager<>(dataFile, this);
        return this;
    }

    public Parameters<T> setDataManager(DataManager<T> dataManager) {
        this.dataManager = dataManager;
        return this;
    }

    public File getPluginsPath() {
        return pluginsPath;
    }

    public Parameters<T> setPluginsPath(File pluginsPath) {
        this.pluginsPath = pluginsPath;
        return this;
    }
}