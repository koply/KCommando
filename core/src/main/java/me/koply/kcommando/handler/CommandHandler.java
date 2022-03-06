package me.koply.kcommando.handler;

import me.koply.kcommando.KCommando;
import me.koply.kcommando.integration.Integration;
import me.koply.kcommando.internal.Kogger;
import me.koply.kcommando.internal.boxes.CommandBox;
import me.koply.kcommando.internal.boxes.SimilarBox;
import me.koply.kcommando.internal.util.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

public class CommandHandler {

    public static class Options {
        public final Integration integration;
        public final String prefix;
        public final long cooldown;
        public final boolean useCaseSensitivity;
        public final boolean readBotMessages;
        public final boolean isAllowSpacesInPrefix;

        public Options(Integration integration, String prefix, long cooldown, boolean useCaseSensitivity, boolean readBotMessages, boolean isAllowSpacesInPrefix) {
            this.integration = integration;
            this.prefix = prefix;
            this.cooldown = cooldown;
            this.useCaseSensitivity = useCaseSensitivity;
            this.readBotMessages = readBotMessages;
            this.isAllowSpacesInPrefix = isAllowSpacesInPrefix;
        }
    }

    public final Map<String, CommandBox> commands;
    public final CommandHandler.Options options;

    public CommandHandler(Map<String, CommandBox> commands, CommandHandler.Options options) {
        this.commands = commands;
        this.options = options;
        this.customPrefixes = options.integration.getCustomGuildPrefixes();
        this.blacklistedUsers = options.integration.getBlacklistedUsers();
    }

    private SimilarBox similarBox; // getter setter

    // guildID, prefixes
    private final ConcurrentMap<Long, Set<String>> customPrefixes;

    // userID's
    private final Set<Long> blacklistedUsers;

    public static class Parameters {
        public final Object event;
        public final String userName;
        public final long userId;
        public final String rawCommand;
        public final String guildName;
        public final long guildID;

        public Parameters(Object event, String userName,
                          long userId, String rawCommand,
                          String guildName, long guildID) {
            this.event = event;
            this.userName = userName;
            this.userId = userId;
            this.rawCommand = rawCommand;
            this.guildName = guildName;
            this.guildID = guildID;
        }
    }

    private void callSimilarCallback(Object event, SimilarBox box, String command) {
        if (box == null) return;
        AbstractCollection<String> list = box.listType == SimilarBox.SimilarListType.LIST ? new ArrayList<>() : new HashSet<>();

        for (Map.Entry<String, CommandBox> entry : commands.entrySet()) {
            double similarity = StringUtil.similarity(entry.getKey(), command);
            if (similarity >= 0.5) {
                list.add(entry.getKey());
            }
        }

        try {
            if (box.usedCommand)
                box.method.invoke(box.instance, event, list, command);
            else
                box.method.invoke(box.instance, event, list);
        } catch (InvocationTargetException | IllegalAccessException e) {
            Kogger.warn("An error occur while calling similar callback. Stacktrace:");
            e.printStackTrace();
        }
    }

    /**
     * @param commandRaw raw command string
     * @param guildID guild id for check prefix
     * @return if prefix correct returns prefix's length.
     */
    protected int checkPrefix(String commandRaw, long guildID) {
        Set<String> prefixes = customPrefixes.get(guildID);
        if (prefixes != null) {
            for (String prefix : prefixes) // TODO: change this block with Set#contains
                if (commandRaw.startsWith(prefix))
                    return prefix.length();
        } else if (commandRaw.startsWith(options.prefix)) {
            return options.prefix.length();
        }
        return -1;
    }

    // TODO: maybe add detailed log messages for everything
    // TODO: ownerOnly, guildOnly, privateOnly, customCooldown
    // TODO: callbacks for wrong usages (with annotation)
    public boolean process(Parameters p) {
        long authorID = p.userId;
        if (blacklistedUsers.contains(authorID)) return false;

        String rawCommand = p.rawCommand;
        int resultPrefix = checkPrefix(rawCommand, p.guildID);
        if (resultPrefix == -1) return false;

        String[] cmdArgs = rawCommand.substring(resultPrefix).split(" ");

        if (KCommando.verbose)
            Kogger.info(String.format("Command received | User: %s | Guild: %s | Command: %s", p.userName, p.guildName, rawCommand));

        String command = options.useCaseSensitivity ? cmdArgs[0] : cmdArgs[0].toLowerCase(Locale.ROOT);
        CommandBox box = commands.get(command);

        if (box == null) {
            callSimilarCallback(p.event, similarBox, command);
            return false;
        }

        String prefix = rawCommand.substring(0,resultPrefix);

        try { switch (box.commandType) {
            case EVENT:
                box.method.invoke(box.instance, p.event);
                break;
            case EVENT_ARGS:
                box.method.invoke(box.instance, p.event, cmdArgs);
                break;
            case EVENT_ARGS_PREFIX:
                box.method.invoke(box.instance, p.event, cmdArgs, prefix);
                break;
            default:
                Kogger.warn("An impossible situation happened.");
        }} catch (InvocationTargetException | IllegalAccessException e) {
            Kogger.warn("An error occured while handling the command. Stacktrace:");
            e.printStackTrace();
        }
        return true;
    }

    public void setSimilarBox(SimilarBox similarBox) {
        this.similarBox = similarBox;
    }
}