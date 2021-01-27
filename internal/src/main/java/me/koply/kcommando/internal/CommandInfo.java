package me.koply.kcommando.internal;

import me.koply.kcommando.internal.annotations.Commando;

public final class CommandInfo<T> {
    private String name;
    private String[] aliases;
    private String description;
    private boolean privateOnly, guildOnly, ownerOnly, sync, onlyArguments;
    private KRunnable<T> onFalseCallback, privateOnlyCallback, guildOnlyCallback, ownerOnlyCallback, cooldownCallback;

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

    public final KRunnable<T> getOnFalseCallback() {
        return onFalseCallback;
    }

    public final CommandInfo<T> setOnFalseCallback(KRunnable<T> onFalseCallback) {
        this.onFalseCallback = onFalseCallback;
        return this;
    }

    public final KRunnable<T> getPrivateOnlyCallback() {
        return privateOnlyCallback;
    }

    public final CommandInfo<T> setPrivateOnlyCallback(KRunnable<T> privateOnlyCallback) {
        this.privateOnlyCallback = privateOnlyCallback;
        return this;
    }

    public final KRunnable<T> getGuildOnlyCallback() {
        return guildOnlyCallback;
    }

    public final CommandInfo<T> setGuildOnlyCallback(KRunnable<T> guildOnlyCallback) {
        this.guildOnlyCallback = guildOnlyCallback;
        return this;
    }

    public final KRunnable<T> getOwnerOnlyCallback() {
        return ownerOnlyCallback;
    }

    public final CommandInfo<T> setOwnerOnlyCallback(KRunnable<T> ownerOnlyCallback) {
        this.ownerOnlyCallback = ownerOnlyCallback;
        return this;
    }

    public KRunnable<T> getCooldownCallback() {
        return cooldownCallback;
    }

    public CommandInfo<T> setCooldownCallback(KRunnable<T> cooldownCallback) {
        this.cooldownCallback = cooldownCallback;
        return this;
    }
}