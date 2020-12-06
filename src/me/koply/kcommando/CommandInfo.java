package me.koply.kcommando;

import me.koply.kcommando.internal.Commander;
import me.koply.kcommando.internal.KRunnable;

public final class CommandInfo {
    private String name;
    private String[] aliases;
    private String description;
    private boolean privateOnly, guildOnly, ownerOnly, sync;
    private KRunnable onFalseCallback, privateOnlyCallback, guildOnlyCallback, ownerOnlyCallback, cooldownCallback;

    protected final void initialize(Commander ant) {
        name = ant.name();
        aliases = ant.aliases();
        description = ant.description();
        privateOnly = ant.privateOnly();
        guildOnly = ant.guildOnly();
        ownerOnly = ant.ownerOnly();
        sync = ant.sync();
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

    public final KRunnable getOnFalseCallback() {
        return onFalseCallback;
    }

    public final CommandInfo setOnFalseCallback(KRunnable onFalseCallback) {
        this.onFalseCallback = onFalseCallback;
        return this;
    }

    public final KRunnable getPrivateOnlyCallback() {
        return privateOnlyCallback;
    }

    public final CommandInfo setPrivateOnlyCallback(KRunnable privateOnlyCallback) {
        this.privateOnlyCallback = privateOnlyCallback;
        return this;
    }

    public final KRunnable getGuildOnlyCallback() {
        return guildOnlyCallback;
    }

    public final CommandInfo setGuildOnlyCallback(KRunnable guildOnlyCallback) {
        this.guildOnlyCallback = guildOnlyCallback;
        return this;
    }

    public final KRunnable getOwnerOnlyCallback() {
        return ownerOnlyCallback;
    }

    public final CommandInfo setOwnerOnlyCallback(KRunnable ownerOnlyCallback) {
        this.ownerOnlyCallback = ownerOnlyCallback;
        return this;
    }

    public KRunnable getCooldownCallback() {
        return cooldownCallback;
    }

    public CommandInfo setCooldownCallback(KRunnable cooldownCallback) {
        this.cooldownCallback = cooldownCallback;
        return this;
    }
}