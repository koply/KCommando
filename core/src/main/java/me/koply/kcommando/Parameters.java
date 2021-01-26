package me.koply.kcommando;

import me.koply.kcommando.integration.Integration;

import java.io.File;
import java.util.*;

public final class Parameters<T> {
    private Integration<T> integration;

    private String packagePath, prefix;

    private long cooldown = 0L;

    private Map<String, String> groupLocales;

    private final Set<String> owners = new HashSet<>();

    private Map<String, CommandToRun<T>> commandMethods;

    private boolean readBotMessages;

    private Optional<Locale> caseSensitivity = Optional.empty();
    private Optional<DataManager<T>> dataManager = Optional.empty();

    public final Integration<T> getIntegration() {
        return integration;
    }

    public final Parameters<T> setIntegration(Integration<T> integration) {
        this.integration = integration;
        return this;
    }

    public final String getPackagePath() {
        return packagePath;
    }

    public final Parameters<T> setPackagePath(String packagePath) {
        this.packagePath = packagePath;
        return this;
    }

    public final String getPrefix() {
        return prefix;
    }

    public final Parameters<T> setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public final long getCooldown() {
        return cooldown;
    }

    public final Parameters<T> setCooldown(long cooldown) {
        this.cooldown = cooldown;
        return this;
    }

    public final Map<String, String> getGroupLocales() {
        return groupLocales;
    }

    public final Parameters<T> setGroupLocales(Map<String, String> groupLocales) {
        this.groupLocales = groupLocales;
        return this;
    }

    public final Parameters<T> setOwners(String...owners) {
        this.owners.addAll(Arrays.asList(owners));
        return this;
    }

    public final Set<String> getOwners() {
        return owners;
    }

    public final Map<String, CommandToRun<T>> getCommandMethods() {
        return commandMethods;
    }

    public final Parameters<T> setCommandMethods(Map<String, CommandToRun<T>> commandMethods) {
        this.commandMethods = commandMethods;
        return this;
    }

    public final boolean isReadBotMessages() {
        return readBotMessages;
    }

    public final Parameters<T> setReadBotMessages(boolean readBotMessages) {
        this.readBotMessages = readBotMessages;
        return this;
    }

    public final Optional<Locale> getCaseSensitivity() {
        return caseSensitivity;
    }

    public final Parameters<T> setCaseSensitivity(Locale caseSensitivity) {
        this.caseSensitivity = Optional.of(caseSensitivity);
        return this;
    }

    public final Optional<DataManager<T>> getDataManager() {
        return dataManager;
    }

    public final Parameters<T> setDataManager(Optional<DataManager<T>> dataManager) {
        this.dataManager = dataManager;
        return this;
    }

    public final Parameters<T> setDataFile(File dataFile) {
        this.dataManager = Optional.of(new DataManager<>(dataFile, this));
        return this;
    }

    public final Parameters<T> setDataManager(DataManager<T> dataManager) {
        this.dataManager = Optional.of(dataManager);
        return this;
    }
}