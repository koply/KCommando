package me.koply.kcommando.integration;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class Integration implements KIntegration {

    public final long selfId;
    protected Integration(final long selfId) {
        this.selfId = selfId;
    }

    // for set the custom guild prefixes
    final ConcurrentMap<Long, Set<String>> customGuildPrefixes = new ConcurrentHashMap<>();
    public final Map<Long, Set<String>> getCustomGuildPrefixes() {
        return Collections.unmodifiableMap(this.customGuildPrefixes);
    }

    /**
     * Adds a custom prefix to the server, but the existing prefix causes it to lose functionality on that server.
     * @param guildID id number of the server to add prefix
     * @param prefix prefix to add
     */
    public void addCustomPrefix(final long guildID, final String prefix) {
        this.customGuildPrefixes.computeIfAbsent(guildID, aLong -> new HashSet<>());
        this.customGuildPrefixes.get(guildID).add(prefix);
    }

    /**
     * Removes a custom prefix from the server
     * @param guildID id number of the server to remove prefix
     * @param prefix prefix to remove
     */
    public void removeCustomPrefix(final long guildID, final String prefix) {
        this.customGuildPrefixes.computeIfPresent(guildID, (aLong, strings) -> {
            final Set<String> temp = new HashSet<>(strings);
            temp.remove(prefix);
            return temp;
        });
        if (this.customGuildPrefixes.get(guildID).isEmpty()) this.customGuildPrefixes.remove(guildID);
    }
    /**
     * Removes guild from the custom prefix map
     * @param guildID id number of the server to disable custom prefix
     */
    public void clearCustomPrefixes(final long guildID) {
        this.customGuildPrefixes.remove(guildID);
    }

    // block a user from all commands
    final Set<Long> blacklistedUsers = Collections.synchronizedSet(new HashSet<>());
    public Set<Long> getBlacklistedUsers() {
        return Collections.unmodifiableSet(this.blacklistedUsers);
    }

}