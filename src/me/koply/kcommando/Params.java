package me.koply.kcommando;

import net.dv8tion.jda.api.JDA;

import java.util.*;

public final class Params {
    private JDA jda;
    private String packagePath, prefix;
    private long cooldown = 0L;
    private HashMap<String, String> groupLocales;
    private final HashSet<String> owners = new HashSet<>();
    private HashMap<String, CommandToRun> commandMethods;
    private boolean readBotMessages;
    private Optional<Locale> caseSensitivity = Optional.empty();

    public final HashMap<String, CommandToRun> getCommandMethods() {
        return commandMethods;
    }

    public final void setCommandMethods(HashMap<String, CommandToRun> commandMethods) {
        this.commandMethods = commandMethods;
    }

    public final JDA getJda() {
        return jda;
    }

    public final void setJda(JDA jda) {
        this.jda = jda;
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

    public final HashMap<String, String> getGroupLocales() {
        return groupLocales;
    }

    public final void setGroupLocales(HashMap<String, String> groupLocales) {
        this.groupLocales = groupLocales;
    }

    public final HashSet<String> getOwners() {
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
}