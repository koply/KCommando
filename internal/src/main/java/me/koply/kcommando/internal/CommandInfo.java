package me.koply.kcommando.internal;

import me.koply.kcommando.internal.annotations.Commando;

import java.util.function.Consumer;

public final class CommandInfo<T> {
    private String name;
    private String[] aliases;
    private String description;
    private boolean privateOnly, guildOnly, ownerOnly, sync, onlyArguments;
    private Consumer<T> onFalseCallback, privateOnlyCallback, guildOnlyCallback, ownerOnlyCallback, cooldownCallback;

    public final void initialize(Commando ant) {
        name = ant.name();
        aliases = ant.aliases();
        description = ant.description();
        privateOnly = ant.privateOnly();
        guildOnly = ant.guildOnly();
        ownerOnly = ant.ownerOnly();
        sync = ant.sync();
        onlyArguments = ant.onlyArguments();
    }

    public final String getName() { return name; }

    public final String[] getAliases() {
        return aliases;
    }

    public final String getDescription() {
        return description;
    }

    public final boolean isPrivateOnly() {
        return privateOnly;
    }

    public final boolean isGuildOnly() {
        return guildOnly;
    }

    public final boolean isOwnerOnly() {
        return ownerOnly;
    }

    public final boolean isSync() {
        return sync;
    }

    public final boolean isOnlyArguments() {
        return onlyArguments;
    }

    public final Consumer<T> getOnFalseCallback() {
        return onFalseCallback;
    }

    public final CommandInfo<T> setOnFalseCallback(Consumer<T> onFalseCallback) {
        this.onFalseCallback = onFalseCallback;
        return this;
    }

    public final Consumer<T> getPrivateOnlyCallback() {
        return privateOnlyCallback;
    }

    public final CommandInfo<T> setPrivateOnlyCallback(Consumer<T> privateOnlyCallback) {
        this.privateOnlyCallback = privateOnlyCallback;
        return this;
    }

    public final Consumer<T> getGuildOnlyCallback() {
        return guildOnlyCallback;
    }

    public final CommandInfo<T> setGuildOnlyCallback(Consumer<T> guildOnlyCallback) {
        this.guildOnlyCallback = guildOnlyCallback;
        return this;
    }

    public final Consumer<T> getOwnerOnlyCallback() {
        return ownerOnlyCallback;
    }

    public final CommandInfo<T> setOwnerOnlyCallback(Consumer<T> ownerOnlyCallback) {
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