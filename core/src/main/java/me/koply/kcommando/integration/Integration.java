package me.koply.kcommando.integration;

import me.koply.kcommando.CommandHandler;
import me.koply.kcommando.internal.KRunnable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Integration {

    // for kcommando core to register Handler to api wrapper
    public abstract void register(CommandHandler commandHandler);

    // for set the custom guild prefixes
    final ConcurrentHashMap<Long, HashSet<String>> customGuildPrefixes = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Long, HashSet<String>> getCustomGuildPrefixes() { return customGuildPrefixes; }

    /**
     * Adds a custom prefix to the server, but the existing prefix causes it to lose functionality on that server.
     * @param guildID id number of the server to add prefix
     * @param prefix prefix to add
     */
    public void addCustomPrefix(long guildID, String prefix) {
        customGuildPrefixes.computeIfAbsent(guildID, aLong -> new HashSet<>());
        customGuildPrefixes.get(guildID).add(prefix);
    }

    /**
     * Removes a custom prefix from the server
     * @param guildID id number of the server to remove prefix
     * @param prefix prefix to remove
     */
    public void removeCustomPrefix(long guildID, String prefix) {
        customGuildPrefixes.computeIfPresent(guildID, (aLong, strings) -> {
            final HashSet<String> temp = new HashSet<>(strings);
            temp.remove(prefix);
            return temp;
        });
        if (customGuildPrefixes.get(guildID).size() == 0) customGuildPrefixes.remove(guildID);
    }

    /**
     * Removes guild from the custom prefix map
     * @param guildID id number of the server to disable custom prefix
     */
    public void disableCustomPrefix(long guildID) {
        customGuildPrefixes.remove(guildID);
    }
    // Maybe TODO: enablable custom prefixes for guilds

    // blacklist user from all commands
    final Set<Long> blacklistedUsers = Collections.synchronizedSet(new HashSet<>());
    public Set<Long> getBlacklistedUsers() { return blacklistedUsers; }

    // blacklist in single guild
    // guild id, blacklisted user id
    final ConcurrentHashMap<Long, HashSet<Long>> blacklistedMembers = new ConcurrentHashMap<>();
    public ConcurrentHashMap<Long, HashSet<Long>> getBlacklistedMembers() { return blacklistedMembers; }

    // blacklist a guild's channel
    final ConcurrentHashMap<Long, HashSet<Long>> blacklistedChannels = new ConcurrentHashMap<>();
    public ConcurrentHashMap<Long, HashSet<Long>> getBlacklistedChannels() { return blacklistedChannels; }

    private KRunnable blacklistCallback;
    public Integration setBlacklistCallback(KRunnable callback) {
        blacklistCallback = callback;
        return this;
    }

    public KRunnable getBlacklistCallback() {
        return blacklistCallback;
    }
}