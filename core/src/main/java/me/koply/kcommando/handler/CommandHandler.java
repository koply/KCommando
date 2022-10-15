package me.koply.kcommando.handler;

import me.koply.kcommando.KCommando;
import me.koply.kcommando.integration.Integration;
import me.koply.kcommando.internal.Kogger;
import me.koply.kcommando.internal.boxes.CommandBox;
import me.koply.kcommando.internal.boxes.FalseBox;
import me.koply.kcommando.internal.boxes.SimilarBox;
import me.koply.kcommando.internal.util.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
        public final Set<Long> ownerIds;
        public final Map<String, FalseBox> falseBoxMap;
        public final String defaultFalseName;

        public Options(Integration integration, String prefix, long cooldown,
                       boolean useCaseSensitivity, boolean readBotMessages, boolean isAllowSpacesInPrefix,
                       Set<Long> ownerIds, Map<String, FalseBox> falseBoxMap, String defaultFalseName) {
            this.integration = integration;
            this.prefix = prefix;
            this.cooldown = cooldown;
            this.useCaseSensitivity = useCaseSensitivity;
            this.readBotMessages = readBotMessages;
            this.isAllowSpacesInPrefix = isAllowSpacesInPrefix;
            this.ownerIds = ownerIds;
            this.falseBoxMap = falseBoxMap;
            this.defaultFalseName = defaultFalseName;
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
        public final long userID;
        public final String rawCommand;
        public final String guildName;
        public final long guildID;

        public Parameters(Object event, String userName,
                          long userID, String rawCommand,
                          String guildName, long guildID) {
            this.event = event;
            this.userName = userName;
            this.userID = userID;
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
            for (String prefix : prefixes)
                if (commandRaw.startsWith(prefix))
                    return prefix.length();
        } else if (commandRaw.startsWith(options.prefix)) {
            return options.prefix.length();
        }
        return -1;
    }

    public boolean process(Parameters p) {
        long authorID = p.userID;
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

        if ((box.annotation.guildOnly() && p.guildID == -1)) {
            return false;
        }

        if (box.annotation.ownerOnly() && !options.ownerIds.contains(p.userID)) {
            return false;
        }

        if (box.annotation.privateOnly() && p.guildID != -1) {
            return false;
        }

        String prefix = rawCommand.substring(0,resultPrefix);

        Object result = invoke((box.commandType.ordinal()+1), box.instance, box.method, p.event, cmdArgs, prefix);

        if (result instanceof Boolean) {
            boolean booleanResult = (boolean) result;
            if (!booleanResult) {
                String methodName = box.annotation.falseMethod().equals("-")
                        ? (options.defaultFalseName.equals("-") ? null : options.defaultFalseName)
                        : box.annotation.falseMethod();

                if (methodName == null) return true;

                FalseBox fbox = options.falseBoxMap.get(methodName);
                System.out.println(methodName);
                if (fbox != null) {
                    if (KCommando.verbose) Kogger.info("Calling false method: " + methodName);
                    invoke(fbox.type.value, fbox.instance, fbox.method, p.event, cmdArgs, prefix);
                }
            }
        }

        return true;
    }

    private Object invoke(int invokeValue, Object obj, Method method, Object...params) {
        // 1,2,3 - 13,14,15 | possible values from BoxType.java
        // 1 -> E
        // 2 -> EA
        // 3 converts to 0 -> EAP
        int val = invokeValue % 3;
        Object result;
        try { switch (val) {
            case 0:
                result = method.invoke(obj, params[0], params[1], params[2]);
                break;
            case 1:
                result = method.invoke(obj, params[0]);
                break;
            case 2:
                result = method.invoke(obj, params[0], params[1]);
                break;
            default:
                Kogger.warn("An impossible situation happened.");
                result = null;
        }} catch (InvocationTargetException | IllegalAccessException e) {
            Kogger.warn("An error occured while handling the command. Stacktrace:");
            e.printStackTrace();
            result = null;
        }

        return result;
    }

    public void setSimilarBox(SimilarBox similarBox) {
        this.similarBox = similarBox;
    }
}