package me.koply.kcommando;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class DataManager {

    private final File dataFile;
    private final Parameters params;
    public DataManager(File dataFile, Parameters params) {
        this.dataFile = dataFile;
        this.params = params;
    }

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

    public void saveDatas() {
        final JSONObject rootJson = new JSONObject();

    }
}