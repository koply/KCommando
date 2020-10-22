package me.koply.kcommando;

import net.dv8tion.jda.api.JDA;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Params {
    private JDA jda;
    private String packagePath, prefix;
    private long cooldown = 0L;
    private HashMap<String, String> groupLocales;
    private List<String> owners;
    private HashMap<String, CommandToRun> commandMethods;
    private boolean readBotMessages;

    public HashMap<String, CommandToRun> getCommandMethods() {
        return commandMethods;
    }

    protected Params setCommandMethods(HashMap<String, CommandToRun> commandMethods) {
        this.commandMethods = commandMethods;
        return this;
    }

    public JDA getJda() {
        return jda;
    }

    protected Params setJda(JDA jda) {
        this.jda = jda;
        return this;
    }

    protected String getPackagePath() {
        return packagePath;
    }

    protected Params setPackagePath(String packagePath) {
        this.packagePath = packagePath;
        return this;
    }

    public String getPrefix() {
        return prefix;
    }

    protected Params setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public long getCooldown() {
        return cooldown;
    }

    protected Params setCooldown(long cooldown) {
        this.cooldown = cooldown;
        return this;
    }

    public HashMap<String, String> getGroupLocales() {
        return groupLocales;
    }

    protected Params setGroupLocales(HashMap<String, String> groupLocales) {
        this.groupLocales = groupLocales;
        return this;
    }

    public List<String> getOwners() {
        return owners;
    }

    protected Params setOwners(String[] owners) {
        this.owners = Arrays.asList(owners);
        return this;
    }

    protected boolean isReadBotMessages() {
        return readBotMessages;
    }

    protected Params setReadBotMessages(boolean readBotMessages) {
        this.readBotMessages = readBotMessages;
        return this;
    }
}