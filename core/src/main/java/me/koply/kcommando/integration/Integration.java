package me.koply.kcommando.integration;

import me.koply.kcommando.CommandHandler;
import me.koply.kcommando.internal.KRunnable;
import me.koply.kcommando.internal.SuggestionsCallback;

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
        // Maybe TODO: enablable custom prefixes for guilds
    }

    // blacklist user from all commands in the bot
    final Set<Long> blacklistedUsers = Collections.synchronizedSet(new HashSet<>());
    public Set<Long> getBlacklistedUsers() { return blacklistedUsers; }

    // blacklist in single guild
    // guild id, blacklisted user id
    final ConcurrentHashMap<Long, HashSet<Long>> blacklistedMembers = new ConcurrentHashMap<>();

    /**
     * @return all blacklisted members as map (guildID, set of the member ids)
     */
    public ConcurrentHashMap<Long, HashSet<Long>> getBlacklistedMembers() {
        return blacklistedMembers;
    }

    /**
     * @param guildID to get blacklisted members on the guild
     * @return all blacklisted members on the selected guild
     */
    public HashSet<Long> getBlacklistedMembers(long guildID) {
        blacklistedMembers.computeIfAbsent(guildID, aLong -> new HashSet<>());
        return blacklistedMembers.get(guildID);
    }

    // blacklist for guild channels
    final ConcurrentHashMap<Long, HashSet<Long>> blacklistedChannels = new ConcurrentHashMap<>();

    /**
     * @return all blacklisted channels as map (guildID, set of the channel ids)
     */
    public ConcurrentHashMap<Long, HashSet<Long>> getBlacklistedChannels() {
        return blacklistedChannels;
    }

    /**
     * @param guildID to get blacklisted channels on the guild
     * @return all blacklisted channels on the selected guild
     */
    public HashSet<Long> getBlacklistedChannels(long guildID) {
        blacklistedChannels.computeIfAbsent(guildID, aLong -> new HashSet<>());
        return blacklistedChannels.get(guildID);
    }

    private KRunnable blacklistCallback;

    /**
     * When a command declined due to a blacklist, runs this callback.
     * @param callback to run
     * @return this integration
     */
    public Integration setBlacklistCallback(KRunnable callback) {
        blacklistCallback = callback;
        return this;
    }

    // internal
    public KRunnable getBlacklistCallback() {
        return blacklistCallback;
    }


    private SuggestionsCallback suggestionsCallback;

    /**
     * When a command declined due to wrong usage and the bot has similar commands calls this callback
     * if it doesn't find similar commands, the HashSet will be empty
     *
     * @param suggestionsCallback to run
     * @return this integration
     */
    public Integration setSuggestionsCallback(SuggestionsCallback suggestionsCallback) {
        this.suggestionsCallback = suggestionsCallback;
        return this;
    }

    // internal
    public SuggestionsCallback getSuggestionsCallback() {
        return suggestionsCallback;
    }
}