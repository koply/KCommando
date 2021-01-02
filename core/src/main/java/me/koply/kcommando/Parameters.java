package me.koply.kcommando;

import me.koply.kcommando.integration.Integration;

import java.io.File;
import java.util.*;

public final class Parameters {
    private Integration integration;
    private String packagePath, prefix;
    private long cooldown = 0L;
    private Map<String, String> groupLocales;
    private final Set<String> owners = new HashSet<>();
    private Map<String, CommandToRun> commandMethods;
    private boolean readBotMessages;
    private Optional<Locale> caseSensitivity = Optional.empty();
    private Optional<DataManager> dataManager = Optional.empty();

    public final Integration getIntegration() {
        return integration;
    }

    public final Parameters setIntegration(Integration integration) {
        this.integration = integration;
        return this;
    }

    public final String getPackagePath() {
        return packagePath;
    }

    public final Parameters setPackagePath(String packagePath) {
        this.packagePath = packagePath;
        return this;
    }

    public final String getPrefix() {
        return prefix;
    }

    public final Parameters setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public final long getCooldown() {
        return cooldown;
    }

    public final Parameters setCooldown(long cooldown) {
        this.cooldown = cooldown;
        return this;
    }

    public final Map<String, String> getGroupLocales() {
        return groupLocales;
    }

    public final Parameters setGroupLocales(Map<String, String> groupLocales) {
        this.groupLocales = groupLocales;
        return this;
    }

    public final Parameters setOwners(String...owners) {
        this.owners.addAll(Arrays.asList(owners));
        return this;
    }

    public final Set<String> getOwners() {
        return owners;
    }

    public final Map<String, CommandToRun> getCommandMethods() {
        return commandMethods;
    }

    public final Parameters setCommandMethods(Map<String, CommandToRun> commandMethods) {
        this.commandMethods = commandMethods;
        return this;
    }

    public final boolean isReadBotMessages() {
        return readBotMessages;
    }

    public final Parameters setReadBotMessages(boolean readBotMessages) {
        this.readBotMessages = readBotMessages;
        return this;
    }

    public final Optional<Locale> getCaseSensitivity() {
        return caseSensitivity;
    }

    public final Parameters setCaseSensitivity(Locale caseSensitivity) {
        this.caseSensitivity = Optional.of(caseSensitivity);
        return this;
    }

    public final Optional<DataManager> getDataManager() {
        return dataManager;
    }

    public final Parameters setDataManager(Optional<DataManager> dataManager) {
        this.dataManager = dataManager;
        return this;
    }

    public final Parameters setDataFile(File dataFile) {
        this.dataManager = Optional.of(new DataManager(dataFile, this));
        return this;
    }

    public final Parameters setDataManager(DataManager dataManager) {
        this.dataManager = Optional.of(dataManager);
        return this;
    }
}