package me.koply.kcommando;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DataManager {

    private final File dataFile;
    private final Parameters params;
    public DataManager(File dataFile, Parameters params) {
        this.dataFile = dataFile;
        this.params = params;

        // windows and linux shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> Util.writeFile(dataFile, getAllDatas().toString()), "TerminateProcess"));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> Util.writeFile(dataFile, getAllDatas().toString()), "Shutdown-thread"));
    }

    /*
     * JSON File Scheme
     * {
     *   "guildDatas": [
     *     {
     *       "id": 00000000L,
     *       "blacklistedMembers": [0000000L, 0000000L],
     *       "blacklistedChannels": [0000000L, 00000000L],
     *       "customPrefixes": ["!", "."]
     *     }
     *   ],
     *   "blacklistedUsers": [0000000L, 0000000L]
     * }
     *
     */

    protected void initDataFile() {
        final String dataString = Util.readFile(dataFile);
        if (dataString.isEmpty()) {
            KCommando.logger.warning("Data file isn't filled. Skipping...");
            return;
        }
        final JSONObject rootJson = new JSONObject(dataString);
        final JSONArray blacklistedUsersArray = rootJson.getJSONArray("blacklistedUsers");
        if (!blacklistedUsersArray.isEmpty()) {
            for (Object blacklistedUser : blacklistedUsersArray) {
                params.getIntegration().getBlacklistedUsers().add((long) blacklistedUser);
            }
        }

        final JSONArray guildDatas = rootJson.optJSONArray("guildDatas");
        final ConcurrentHashMap<Long, HashSet<String>> allCustomPrefixes = params.getIntegration().getCustomGuildPrefixes();
        if (guildDatas != null) {
            for (Object guildData : guildDatas) {
                final JSONObject guildObject = (JSONObject) guildData;
                final long id = guildObject.getLong("id");

                final JSONArray blacklistedMembersArray = guildObject.getJSONArray("blacklistedMembers");
                if (!blacklistedMembersArray.isEmpty()) {
                    final HashSet<Long> blacklistedMembers = params.getIntegration().getBlacklistedMembers(id);
                    for (int i = -1; ++i < blacklistedMembersArray.length();) {
                        blacklistedMembers.add((long) blacklistedMembersArray.get(i));
                    }
                }

                final JSONArray blacklistedChannelsArray = guildObject.getJSONArray("blacklistedChannels");
                if (!blacklistedChannelsArray.isEmpty()) {
                    final HashSet<Long> blacklistedChannels = params.getIntegration().getBlacklistedChannels(id);
                    for (int i = -1; ++i < blacklistedChannelsArray.length();) {
                        blacklistedChannels.add((long) blacklistedChannelsArray.get(i));
                    }
                }

                final JSONArray customPrefixes = guildObject.getJSONArray("customPrefixes");
                if (!customPrefixes.isEmpty()) {
                    allCustomPrefixes.computeIfAbsent(id, aLong -> new HashSet<>());
                    HashSet<String> prefixes = allCustomPrefixes.get(id);
                    for (Object prefix : customPrefixes) {
                        prefixes.add((String) prefix);
                    }
                }
            }
        }
        KCommando.logger.info("Data file readed successfully.");
    }

    protected JSONObject getAllDatas() {
        final JSONObject rootJson = new JSONObject();
        final HashMap<Long, JSONObject> guildObject = new HashMap<>();

        final ConcurrentHashMap<Long, HashSet<Long>> blacklistedMembers = params.getIntegration().getBlacklistedMembers();
        for (Map.Entry<Long, HashSet<Long>> entry : blacklistedMembers.entrySet()) {
            this.dataProcess("blacklistedMembers", guildObject, entry);
        }

        final ConcurrentHashMap<Long, HashSet<Long>> blacklistedChannels = params.getIntegration().getBlacklistedChannels();
        for (Map.Entry<Long, HashSet<Long>> entry : blacklistedChannels.entrySet()) {
            this.dataProcess("blacklistedChannels", guildObject, entry);
        }

        final ConcurrentHashMap<Long, HashSet<String>> customGuildPrefixes = params.getIntegration().getCustomGuildPrefixes();
        for (Map.Entry<Long, HashSet<String>> entry : customGuildPrefixes.entrySet()) {
            this.dataProcess("customPrefixes", guildObject, entry);
        }

        rootJson.put("guildDatas", guildObject);

        final Set<Long> blacklistedUsers = params.getIntegration().getBlacklistedUsers();
        final JSONArray jsonArray = new JSONArray();
        jsonArray.putAll(blacklistedUsers);
        rootJson.put("blacklistedUsers", blacklistedUsers);
        return rootJson;
    }

    private <T> void dataProcess(String key, HashMap<Long, JSONObject> guildObject, Map.Entry<Long, HashSet<T>> entry) {
        JSONObject tempGuild = guildObject.containsKey(entry.getKey()) ? guildObject.get(entry.getKey()) : new JSONObject();
        if (tempGuild.opt("id") != null) tempGuild.put("id", entry.getKey());
        JSONArray tempPrefixes = new JSONArray();
        tempPrefixes.putAll(entry.getValue());
        tempGuild.put(key, tempPrefixes);
        guildObject.put(entry.getKey(), tempGuild);
    }
}