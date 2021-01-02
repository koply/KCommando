package me.koply.kcommando;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
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

        // TODO auto backup

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

    /**
     * fills the data maps from datafile to maps in the params instance (customprefix, blacklist)
     */
    public void initDataFile() {
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

                final JSONArray blacklistedMembersArray = guildObject.optJSONArray("blacklistedMembers");
                if (blacklistedMembersArray != null && !blacklistedMembersArray.isEmpty()) {
                    final HashSet<Long> blacklistedMembers = params.getIntegration().getBlacklistedMembers(id);
                    for (int i = -1; ++i < blacklistedMembersArray.length();) {
                        blacklistedMembers.add((long) blacklistedMembersArray.get(i));
                    }
                }

                final JSONArray blacklistedChannelsArray = guildObject.optJSONArray("blacklistedChannels");
                if (blacklistedChannelsArray != null && !blacklistedChannelsArray.isEmpty()) {
                    final HashSet<Long> blacklistedChannels = params.getIntegration().getBlacklistedChannels(id);
                    for (int i = -1; ++i < blacklistedChannelsArray.length();) {
                        blacklistedChannels.add((long) blacklistedChannelsArray.get(i));
                    }
                }

                final JSONArray customPrefixes = guildObject.optJSONArray("customPrefixes");
                if (customPrefixes != null && !customPrefixes.isEmpty()) {
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

    public JSONObject getAllDatas() {
        final JSONObject rootJson = new JSONObject();
        final HashSet<JSONObject> guildObject = new HashSet<>();

        final ConcurrentHashMap<Long, HashSet<Long>> blacklistedMembers = new ConcurrentHashMap<>(params.getIntegration().getBlacklistedMembers());
        final ConcurrentHashMap<Long, HashSet<Long>> blacklistedChannels = new ConcurrentHashMap<>(params.getIntegration().getBlacklistedChannels());
        final ConcurrentHashMap<Long, HashSet<String>> customGuildPrefixes = new ConcurrentHashMap<>(params.getIntegration().getCustomGuildPrefixes());

        for (Map.Entry<Long, HashSet<Long>> entry : blacklistedMembers.entrySet()) {
            JSONObject tempGuild = new JSONObject();
            tempGuild.put("id", entry.getKey());
            tempGuild.put("blacklistedMembers", getJArrayFromSet(blacklistedMembers.get(entry.getKey())));

            if (blacklistedChannels.containsKey(entry.getKey())) {
                tempGuild.put("blacklistedChannels", getJArrayFromSet(blacklistedChannels.get(entry.getKey())));
                blacklistedChannels.remove(entry.getKey());
            }
            if (customGuildPrefixes.containsKey(entry.getKey())) {
                tempGuild.put("customPrefixes", getJArrayFromSet(customGuildPrefixes.get(entry.getKey())));
                customGuildPrefixes.remove(entry.getKey());
            }
            guildObject.add(tempGuild);
        }

        for (Map.Entry<Long, HashSet<Long>> entry : blacklistedChannels.entrySet()) {
            JSONObject tempGuild = new JSONObject();
            tempGuild.put("id", entry.getKey());
            tempGuild.put("blacklistedChannels", getJArrayFromSet(blacklistedChannels.get(entry.getKey())));

            if (customGuildPrefixes.containsKey(entry.getKey())) {
                tempGuild.put("customPrefixes", getJArrayFromSet(customGuildPrefixes.get(entry.getKey())));
                customGuildPrefixes.remove(entry.getKey());
            }
            guildObject.add(tempGuild);
        }


        for (Map.Entry<Long, HashSet<String>> entry : customGuildPrefixes.entrySet()) {
            JSONObject tempGuild = new JSONObject();
            tempGuild.put("id", entry.getKey());
            tempGuild.put("customPrefixes", getJArrayFromSet(customGuildPrefixes.get(entry.getKey())));
            guildObject.add(tempGuild);
        }

        rootJson.put("guildDatas", guildObject);

        final Set<Long> blacklistedUsers = params.getIntegration().getBlacklistedUsers();
        final JSONArray jsonArray = new JSONArray();
        jsonArray.putAll(blacklistedUsers);
        rootJson.put("blacklistedUsers", blacklistedUsers);
        return rootJson;
    }

    private <T> JSONArray getJArrayFromSet(HashSet<T> set) {
        return new JSONArray().putAll(set);
    }
}