package me.koply.kcommando.integration;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class Integration implements KIntegration {

    public final long selfId;
    protected Integration(long selfId) {
        this.selfId = selfId;
    }

    // for set the custom guild prefixes
    final ConcurrentMap<Long, Set<String>> customGuildPrefixes = new ConcurrentHashMap<>();
    public final ConcurrentMap<Long, Set<String>> getCustomGuildPrefixes() {
        return customGuildPrefixes;
    }

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
            final Set<String> temp = new HashSet<>(strings);
            temp.remove(prefix);
            return temp;
        });
        if (customGuildPrefixes.get(guildID).size() == 0) customGuildPrefixes.remove(guildID);
    }
    /**
     * Removes guild from the custom prefix map
     * @param guildID id number of the server to disable custom prefix
     */
    public void clearCustomPrefixes(long guildID) {
        customGuildPrefixes.remove(guildID);
    }

    // block a user from all commands
    final Set<Long> blacklistedUsers = Collections.synchronizedSet(new HashSet<>());
    public Set<Long> getBlacklistedUsers() {
        return Collections.unmodifiableSet(blacklistedUsers);
    }

}