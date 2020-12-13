package me.koply.kcommando;

import me.koply.kcommando.integration.Integration;

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
    private long selfUserId;

    public final Map<String, CommandToRun> getCommandMethods() {
        return commandMethods;
    }

    public final void setCommandMethods(HashMap<String, CommandToRun> commandMethods) {
        this.commandMethods = commandMethods;
    }

    public final String getPackagePath() {
        return packagePath;
    }

    public final void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

    public final String getPrefix() {
        return prefix;
    }

    public final void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public final long getCooldown() {
        return cooldown;
    }

    public final void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public final Map<String, String> getGroupLocales() {
        return groupLocales;
    }

    public final void setGroupLocales(Map<String, String> groupLocales) {
        this.groupLocales = groupLocales;
    }

    public final Set<String> getOwners() {
        return owners;
    }

    public final void setOwners(String[] owners) {
        this.owners.addAll(Arrays.asList(owners));
    }

    public final boolean isReadBotMessages() {
        return readBotMessages;
    }

    public final void setReadBotMessages(boolean readBotMessages) {
        this.readBotMessages = readBotMessages;
    }

    public Optional<Locale> getCaseSensitivity() { return caseSensitivity; }

    public final void setCaseSensitivity(Locale cs) {
        caseSensitivity = Optional.ofNullable(cs);
    }

    public long getSelfUserId() {
        return selfUserId;
    }

    public void setSelfUserId(long selfUserId) {
        this.selfUserId = selfUserId;
    }

    public Integration getIntegration() {
        return integration;
    }

    public void setIntegration(Integration integration) {
        this.integration = integration;
    }

}