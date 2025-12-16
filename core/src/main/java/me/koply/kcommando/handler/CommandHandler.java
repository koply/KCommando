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

        public Options(final Integration integration, final String prefix, final long cooldown,
                       final boolean useCaseSensitivity, final boolean readBotMessages, final boolean isAllowSpacesInPrefix,
                       final Set<Long> ownerIds, final Map<String, FalseBox> falseBoxMap, final String defaultFalseName) {
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

    public CommandHandler(final Map<String, CommandBox> commands, final CommandHandler.Options options) {
        this.commands = commands;
        this.options = options;
        this.customPrefixes = options.integration.getCustomGuildPrefixes();
        this.blacklistedUsers = options.integration.getBlacklistedUsers();
    }

    private SimilarBox similarBox; // getter setter

    // guildID, prefixes
    private final Map<Long, Set<String>> customPrefixes;

    // userID's
    private final Set<Long> blacklistedUsers;

    public static class Parameters {
        public final Object event;
        public final String userName;
        public final long userID;
        public final String rawCommand;
        public final String guildName;
        public final long guildID;

        public Parameters(final Object event, final String userName,
                          final long userID, final String rawCommand,
                          final String guildName, final long guildID) {
            this.event = event;
            this.userName = userName;
            this.userID = userID;
            this.rawCommand = rawCommand;
            this.guildName = guildName;
            this.guildID = guildID;
        }
    }

    private void callSimilarCallback(final Object event, final SimilarBox box, final String command) {
        if (box == null) return;
        final AbstractCollection<String> list = box.listType == SimilarBox.SimilarListType.LIST ? new ArrayList<>() : new HashSet<>();

        for (final Map.Entry<String, CommandBox> entry : this.commands.entrySet()) {
            final double similarity = StringUtil.similarity(entry.getKey(), command);
            if (similarity >= 0.5) {
                list.add(entry.getKey());
            }
        }

        try {
            if (box.usedCommand)
                box.method.invoke(box.instance, event, list, command);
            else
                box.method.invoke(box.instance, event, list);
        } catch (final InvocationTargetException | IllegalAccessException e) {
            Kogger.warn("An error occur while calling similar callback. Stacktrace:");
            e.printStackTrace();
        }
    }

    /**
     * @param commandRaw raw command string
     * @param guildID guild id for check prefix
     * @return if prefix correct returns prefix's length.
     */
    protected int checkPrefix(final String commandRaw, final long guildID) {
        final Set<String> prefixes = this.customPrefixes.get(guildID);
        if (prefixes != null) {
            for (final String prefix : prefixes)
                if (commandRaw.startsWith(prefix))
                    return prefix.length();
        } else if (commandRaw.startsWith(this.options.prefix)) {
            return this.options.prefix.length();
        }
        return -1;
    }

    public boolean process(final Parameters p) {
        final long authorID = p.userID;
        if (this.blacklistedUsers.contains(authorID)) return false;

        final String rawCommand = p.rawCommand;
        final int resultPrefix = this.checkPrefix(rawCommand, p.guildID);
        if (resultPrefix == -1) return false;

        final String[] cmdArgs = rawCommand.substring(resultPrefix).split(" ");

        if (KCommando.verbose)
            Kogger.info(String.format("Command received | User: %s | Guild: %s | Command: %s", p.userName, p.guildName, rawCommand));

        final String command = this.options.useCaseSensitivity ? cmdArgs[0] : cmdArgs[0].toLowerCase(Locale.ROOT);
        final CommandBox box = this.commands.get(command);

        if (box == null) {
            this.callSimilarCallback(p.event, this.similarBox, command);
            return false;
        }

        if ((box.annotation.guildOnly() && p.guildID == -1)) {
            return false;
        }

        if (box.annotation.ownerOnly() && !this.options.ownerIds.contains(p.userID)) {
            return false;
        }

        if (box.annotation.privateOnly() && p.guildID != -1) {
            return false;
        }

        final String prefix = rawCommand.substring(0,resultPrefix);

        final Object invokeResult = this.invoke((box.commandType.ordinal()+1), box.instance, box.method, p.event, cmdArgs, prefix);

        if (invokeResult instanceof Boolean) {
            if (!((boolean) invokeResult)) {
                final String methodName = box.annotation.falseMethod().equals("-")
                        ? (this.options.defaultFalseName.equals("-") ? null : this.options.defaultFalseName)
                        : box.annotation.falseMethod();

                if (methodName == null) return true;

                final FalseBox fbox = this.options.falseBoxMap.get(methodName);
                System.out.println(methodName);
                if (fbox != null) {
                    if (KCommando.verbose) Kogger.info("Calling false method: " + methodName);
                    this.invoke(fbox.type.value, fbox.instance, fbox.method, p.event, cmdArgs, prefix);
                }
            }
        }

        return true;
    }

    private Object invoke(final int invokeValue, final Object obj, final Method method, final Object...params) {
        // 1,2,3 - 13,14,15 | possible values from BoxType.java
        // 1 -> E
        // 2 -> EA
        // 3 converts to 0 -> EAP
        final int val = invokeValue % 3;
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
        }} catch (final InvocationTargetException | IllegalAccessException e) {
            Kogger.warn("An error occured while handling the command. Stacktrace:");
            e.printStackTrace();
            result = null;
        }

        return result;
    }

    public void setSimilarBox(final SimilarBox similarBox) {
        this.similarBox = similarBox;
    }
}