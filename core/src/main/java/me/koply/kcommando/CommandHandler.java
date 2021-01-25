package me.koply.kcommando;

import me.koply.kcommando.internal.CommandType;
import me.koply.kcommando.internal.KRunnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandHandler {

    private final Parameters params;
    private final long selfUserID;
    private final ConcurrentHashMap<Long, HashSet<String>> customPrefixes;
    private final Map<String, CommandToRun> commandsMap;
    private final ConcurrentMap<Long, Long> cooldownList = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public CommandHandler(Parameters params) {
        this.params = params;
        commandsMap = params.getCommandMethods();
        customPrefixes = params.getIntegration().getCustomGuildPrefixes();
        selfUserID = params.getIntegration().getSelfID();

        CronService.getInstance().addRunnable(() -> {
            long current = System.currentTimeMillis();
            int i = 0;
            for (Map.Entry<Long, Long> entry : cooldownList.entrySet()) {
                if (current - entry.getValue() >= params.getCooldown()) {
                    cooldownList.remove(entry.getKey());
                    i++;
                }
            }
            if (i == 0) return;
            KCommando.logger.info("CooldownList cleaned. " + i + " entries deleted.");
        });

        KCommando.logger.info("initialized.");
    }

    /**
     * @param guildID current guild's id, if command couldn't have guild its must be -1
     * @param authorID current author's id.
     * @param channelID current guild text channel id. if command couldn't have channel its must be -1
     * @return if command blacklisted returns true.
     */
    protected boolean blacklistCheck(long guildID, long authorID, long channelID) {
        HashSet<Long> blacklistedMembers = guildID == -1 ? null : params.getIntegration().getBlacklistedMembers().getOrDefault(guildID, null);
        boolean isBlacklistedMember = blacklistedMembers != null && blacklistedMembers.contains(authorID);

        HashSet<Long> blacklistedChannels = channelID == -1 ? null : params.getIntegration().getBlacklistedChannels().getOrDefault(guildID, null);
        boolean isBlacklistedChannel = blacklistedChannels != null && blacklistedChannels.contains(channelID);

        return params.getIntegration().getBlacklistedUsers().contains(authorID) ||
                isBlacklistedMember || isBlacklistedChannel;
    }

    /**
     * @param commandRaw raw command string
     * @param guildID guild id for check prefix
     * @return if prefix correct returns prefix's length.
     */
    protected int checkPrefix(String commandRaw, long guildID) {
        if (customPrefixes.containsKey(guildID)) {
            for (String prefix : customPrefixes.get(guildID)) {
                if (commandRaw.startsWith(prefix)) return prefix.length();
            }
            return -1;
        } else {
            return commandRaw.startsWith(params.getPrefix()) ? params.getPrefix().length() : -1;
        }
    }

    /**
     * @param command pure command string
     * @return commandsMaps contains command param
     */
    protected boolean containsCommand(String command) {
         return commandsMap.containsKey(command);
    }

    /**
     * command check for guildOnly, privateOnly, ownerOnly
     *
     * @param info commandInfo object from current command
     * @param cpp command process parameters for usage
     * @return if command correct, returns false.
     */
    @SuppressWarnings("unchecked")
    protected boolean commandCheck(CommandInfo info, CProcessParameters cpp) {
        if (info.isGuildOnly() && cpp.getGuildID() == -1) {
            KCommando.logger.info("GuildOnly command used from private channel");
            if (info.getGuildOnlyCallback() != null) {
                executorService.submit(() -> info.getGuildOnlyCallback().run(cpp.getEvent()));
            }
            return true;
        }
        if (info.isPrivateOnly() && cpp.getGuildID() != -1) {
            KCommando.logger.info("PrivateOnly command used from guild channel");
            if (info.getPrivateOnlyCallback() != null) {
                executorService.submit(() -> info.getPrivateOnlyCallback().run(cpp.getEvent()));
            }
            return true;
        }
        if (info.isOwnerOnly() && !params.getOwners().contains(cpp.getAuthor().getId() + "")) {
            KCommando.logger.info("OwnerOnly command used by normal user.");
            if (info.getOwnerOnlyCallback() != null) {
                executorService.submit(() -> info.getOwnerOnlyCallback().run(cpp.getEvent()));
            }
            return true;
        }
        return false;
    }

    /**
     * @param info current command info
     * @param cpp command process parameters
     * @param authorID current commands author id
     * @return if cooldown is correct returns false
     */
    protected boolean cooldownCheck(CommandInfo info, CProcessParameters cpp, long authorID) {
        if (cooldownMapChecker(authorID, cooldownList, params.getCooldown()) && !params.getOwners().contains(authorID + "")) {
            KCommando.logger.info("Last command has been declined due to cooldown check");
            if (info.getCooldownCallback() != null) {
                executorService.submit(() -> info.getCooldownCallback().run(cpp.getEvent()));
            }
            return true;
        }
        return false;
    }

    protected void findSimilars(String command, Object event) {
        HashSet<CommandInfo> similarCommands = new HashSet<>();
        for (Map.Entry<String, CommandToRun> entry : commandsMap.entrySet()) {
            double similarity = Util.similarity(entry.getKey(), command);
            if (similarity >= 0.5) {
                similarCommands.add(entry.getValue().getClazz().getInfo());
            }
        }
        params.getIntegration().getSuggestionsCallback().run(event, similarCommands);
    }

    public void processCommand(final CProcessParameters cpp) {
        final long authorID = cpp.getAuthor().getId();
        if (authorID == selfUserID ||
                (!params.isReadBotMessages() && cpp.getAuthor().isBot()) ||
                cpp.isWebhookMessage())
            return;

        if (blacklistCheck(cpp.getGuildID(), authorID, cpp.getChannelID())) {
            if (params.getIntegration().getBlacklistCallback() != null)
                params.getIntegration().getBlacklistCallback().run(cpp.getEvent());
            return;
        }

        final String commandRaw = cpp.getRawCommand();
        final int resultPrefix = checkPrefix(commandRaw, cpp.getGuildID());
        if (resultPrefix == -1) return;
        
        final String prefix = commandRaw.substring(0,resultPrefix);
        final String[] cmdArgs = commandRaw.substring(resultPrefix).split(" ");
        KCommando.logger.info(String.format("Command received | User: %s | Guild: %s | Command: %s", cpp.getAuthor().getName(), cpp.getGuildName(), commandRaw));
        final String command = params.getCaseSensitivity().isPresent() ? cmdArgs[0] : cmdArgs[0].toLowerCase();

        if (!containsCommand(command)) {
            if (params.getIntegration().getSuggestionsCallback() != null) {
                findSimilars(command, cpp.getEvent());
            }
            return;
        }

        final CommandToRun ctr = commandsMap.get(command);
        final CommandInfo info = ctr.getClazz().getInfo();

        if (commandCheck(info, cpp) || cooldownCheck(info, cpp, authorID)) {
            return;
        }
        runCommand(info, ctr, cpp, cmdArgs, prefix);
    }

    /**
     * runs the command
     *
     * @param info CommandInfo object for get some information about the command
     * @param ctr CommandToRun object for run the command
     * @param cpp Command parameters from api
     * @param cmdArgs raw command text splitted by spaces and cutted the prefix.
     * @param prefix the current prefix
     */
    protected void runCommand(CommandInfo info, CommandToRun ctr, CProcessParameters cpp, String[] cmdArgs, String prefix) {
        // async runner first. because async chance is higher than sync commands
        final long firstTime = System.currentTimeMillis();
        cooldownList.put(cpp.getAuthor().getId(), firstTime);
        if (!info.isSync()) {
            try {
                executorService.submit(() -> {
                    KCommando.logger.info("Last command has been submitted to ExecutorService.");
                    internalCaller(ctr, cpp.getEvent(), cmdArgs, info, prefix);
                    KCommando.logger.info("Last command took " + (System.currentTimeMillis() - firstTime) + "ms to execute.");
                });
            } catch (Throwable t) { t.printStackTrace(); }
        } else {
            internalCaller(ctr, cpp.getEvent(), cmdArgs, info, prefix);
            KCommando.logger.info("Last command took " + (System.currentTimeMillis() - firstTime) + "ms to execute.");
        }
    }

    protected void internalCaller(CommandToRun ctr, Object event, String[] args, CommandInfo info, String prefix) {
        final KRunnable onFalse = info.getOnFalseCallback();
        try {
            HashMap<String, CommandToRun.MethodToRun> argumentMethods = ctr.getArgumentMethods();
            if (args.length > 1) {
                if (argumentMethods.containsKey(args[1])) {
                    CommandToRun.MethodToRun mtr = argumentMethods.get(args[1]);
                    argWrapper(mtr.getType(), mtr.getMethod(), ctr.getClazz(), event, onFalse, args, prefix);
                } else if (info.isOnlyArguments()) {
                    if (onFalse != null) onFalse.run(event);
                } else {
                    handleWrapper(ctr.getType(), ctr.getClazz(), event, onFalse, args, prefix);
                }
            } else {
                handleWrapper(ctr.getType(), ctr.getClazz(), event, onFalse, args, prefix);
            }
        } catch (Throwable t) { KCommando.logger.info("Command crashed! Message: " + t.getMessage() + "\n" + Arrays.toString(t.getStackTrace())); }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void handleWrapper(CommandType type, Command clazz,
                               Object event, KRunnable onFalse,
                               String[] args, String prefix) {
        switch (type.value) {
            case 0x01:
                if (!clazz.handle(event) && onFalse != null) {
                    onFalse.run(event);
                }
                break;
            case 0x02:
                if (!clazz.handle(event, args) && onFalse != null) {
                    onFalse.run(event);
                }
                break;
            case 0x03:
                if (!clazz.handle(event, args, prefix) && onFalse != null) {
                    onFalse.run(event);
                }
                break;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void argWrapper(CommandType type, Method method,
                            Command clazz, Object event,
                            KRunnable onFalse, String[] args,
                            String prefix) throws InvocationTargetException, IllegalAccessException {
        switch (type.value) {
            case 0x01:
                if (!(boolean)method.invoke(clazz, event) && onFalse != null) {
                    onFalse.run(event);
                }
                break;
            case 0x02:
                if (!(boolean)method.invoke(clazz, event, args) && onFalse != null) {
                    onFalse.run(event);
                }
                break;
            case 0x03:
                if (!(boolean)method.invoke(clazz, event, args, prefix) && onFalse != null) {
                    onFalse.run(event);
                }
                break;
        }
    }

    protected boolean cooldownMapChecker(long userID, ConcurrentMap<Long, Long> cooldownList, long cooldown) {
        long listTime = cooldownList.getOrDefault(userID, 0L);
        return listTime != 0 && System.currentTimeMillis() - listTime <= cooldown;
    }
}