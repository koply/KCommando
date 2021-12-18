package me.koply.kcommando.internal;

import me.koply.kcommando.internal.annotations.Commando;

import java.util.function.Consumer;

@SuppressWarnings("ALL")
public final class CommandInfo<T> {
    private String name;
    private String[] aliases;
    private String description;
    private boolean privateOnly, guildOnly, ownerOnly, sync, onlyArguments;
    private Consumer<T> onFalseCallback, privateOnlyCallback, guildOnlyCallback, ownerOnlyCallback, cooldownCallback;

    public void initialize(Commando ant) {
        name = ant.name();
        aliases = ant.aliases();
        description = ant.description();
        privateOnly = ant.privateOnly();
        guildOnly = ant.guildOnly();
        ownerOnly = ant.ownerOnly();
        sync = ant.sync();
        onlyArguments = ant.onlyArguments();
    }

    public String getName() { return name; }

    public String[] getAliases() {
        return aliases;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPrivateOnly() {
        return privateOnly;
    }

    public boolean isGuildOnly() {
        return guildOnly;
    }

    public boolean isOwnerOnly() {
        return ownerOnly;
    }

    public boolean isSync() {
        return sync;
    }

    public boolean isOnlyArguments() {
        return onlyArguments;
    }

    public Consumer<T> getOnFalseCallback() {
        return onFalseCallback;
    }

    public CommandInfo<T> setOnFalseCallback(Consumer<T> onFalseCallback) {
        this.onFalseCallback = onFalseCallback;
        return this;
    }

    public Consumer<T> getPrivateOnlyCallback() {
        return privateOnlyCallback;
    }

    public CommandInfo<T> setPrivateOnlyCallback(Consumer<T> privateOnlyCallback) {
        this.privateOnlyCallback = privateOnlyCallback;
        return this;
    }

    public Consumer<T> getGuildOnlyCallback() {
        return guildOnlyCallback;
    }

    public CommandInfo<T> setGuildOnlyCallback(Consumer<T> guildOnlyCallback) {
        this.guildOnlyCallback = guildOnlyCallback;
        return this;
    }

    public Consumer<T> getOwnerOnlyCallback() {
        return ownerOnlyCallback;
    }

    public CommandInfo<T> setOwnerOnlyCallback(Consumer<T> ownerOnlyCallback) {
        this.ownerOnlyCallback = ownerOnlyCallback;
        return this;
    }

    public Consumer<T> getCooldownCallback() {
        return cooldownCallback;
    }

    public CommandInfo<T> setCooldownCallback(Consumer<T> cooldownCallback) {
        this.cooldownCallback = cooldownCallback;
        return this;
    }
}