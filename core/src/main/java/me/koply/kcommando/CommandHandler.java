package me.koply.kcommando;

import me.koply.kcommando.internal.Command;
import me.koply.kcommando.internal.CommandInfo;
import me.koply.kcommando.internal.CommandType;
import me.koply.kcommando.internal.KRunnable;
import me.koply.kcommando.util.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandHandler<T> {

    private final Parameters<T> params;
    private final long selfUserID;
    private final ConcurrentMap<Long, Set<String>> customPrefixes;
    private final Map<String, CommandToRun<T>> commandsMap;
    private final ConcurrentMap<Long, Long> cooldownList = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public CommandHandler(Parameters<T> params) {
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
        }, 5);

        KCommando.logger.info("initialized.");
    }

    /**
     * @param guildID current guild's id, if command couldn't have guild its must be -1
     * @param authorID current author's id.
     * @param channelID current guild text channel id. if command couldn't have channel its must be -1
     * @return if command blacklisted returns true.
     */
    protected boolean blacklistCheck(long guildID, long authorID, long channelID) {
        Set<Long> blacklistedMembers = guildID == -1 ? null : params.getIntegration().getBlacklistedMembers().getOrDefault(guildID, null);
        boolean isBlacklistedMember = blacklistedMembers != null && blacklistedMembers.contains(authorID);

        Set<Long> blacklistedChannels = channelID == -1 ? null : params.getIntegration().getBlacklistedChannels().getOrDefault(guildID, null);
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
                if (commandRaw.startsWith(prefix))
                    return prefix.length();
            }

        } else if (commandRaw.startsWith(params.getPrefix())) {
            return  params.getPrefix().length();
        }

        return -1;
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
     * @param params command process parameters for usage
     * @return if command correct, returns false.
     */
    protected boolean commandCheck(CommandInfo<T> info, CProcessParameters<T> params) {
        if (info.isGuildOnly() && params.getGuildID() == -1) {
            KCommando.logger.info("GuildOnly command used from private channel");

            if (info.getGuildOnlyCallback() != null) {
                executorService.submit(() -> info.getGuildOnlyCallback().run(params.getEvent()));
            }

            return true;
        }

        if (info.isPrivateOnly() && params.getGuildID() != -1) {
            KCommando.logger.info("PrivateOnly command used from guild channel");

            if (info.getPrivateOnlyCallback() != null) {
                executorService.submit(() -> info.getPrivateOnlyCallback().run(params.getEvent()));
            }

            return true;
        }

        if (info.isOwnerOnly() && !this.params.getOwners().contains(params.getAuthor().getId() + "")) {
            KCommando.logger.info("OwnerOnly command used by normal user.");

            if (info.getOwnerOnlyCallback() != null) {
                executorService.submit(() -> info.getOwnerOnlyCallback().run(params.getEvent()));
            }

            return true;
        }

        return false;
    }

    /**
     * @param info current command info
     * @param params command process parameters
     * @param authorID current commands author id
     * @return if cooldown is correct returns false
     */
    protected boolean cooldownCheck(CommandInfo<T> info, CProcessParameters<T> params, long authorID) {
        if (cooldownMapChecker(authorID, cooldownList, this.params.getCooldown()) && !this.params.getOwners().contains(authorID + "")) {
            KCommando.logger.info("Last command has been declined due to cooldown check");

            if (info.getCooldownCallback() != null) {
                executorService.submit(() -> info.getCooldownCallback().run(params.getEvent()));
            }

            return true;
        }

        return false;
    }

    protected void findSimilars(String command, T event) {
        Set<CommandInfo<T>> similarCommands = new HashSet<>();

        for (Map.Entry<String, CommandToRun<T>> entry : commandsMap.entrySet()) {

            double similarity = StringUtil.similarity(entry.getKey(), command);
            if (similarity >= 0.5) {
                similarCommands.add(entry.getValue().getClazz().getInfo());
            }

        }

        params.getIntegration().getSuggestionsCallback().run(event, similarCommands);
    }

    public void processCommand(final CProcessParameters<T> params) {

        final long authorID = params.getAuthor().getId();
        if (authorID == selfUserID) return;

        if (!this.params.isReadBotMessages() && params.getAuthor().isBot()) return;

        if (params.isWebhookMessage()) return;

        if (blacklistCheck(params.getGuildID(), authorID, params.getChannelID())) {
            KRunnable<T> callback = this.params.getIntegration().getBlacklistCallback();

            if (callback != null) {
                callback.run(params.getEvent());
            }

            return;
        }

        final String commandRaw = params.getRawCommand();
        final int resultPrefix = checkPrefix(commandRaw, params.getGuildID());
        if (resultPrefix == -1) return;
        
        final String prefix = commandRaw.substring(0,resultPrefix);
        final String[] cmdArgs = commandRaw.substring(resultPrefix).split(" ");

        KCommando.logger.info(String.format("Command received | User: %s | Guild: %s | Command: %s", params.getAuthor().getName(), params.getGuildName(), commandRaw));

        final String command = this.params.getCaseSensitivity().isPresent() ? cmdArgs[0] : cmdArgs[0].toLowerCase();

        if (!containsCommand(command)) {
            if (this.params.getIntegration().getSuggestionsCallback() != null) {
                findSimilars(command, params.getEvent());
            }
            return;
        }

        final CommandToRun<T> ctr = commandsMap.get(command);
        final CommandInfo<T> info = ctr.getClazz().getInfo();

        if (commandCheck(info, params) || cooldownCheck(info, params, authorID)) {
            return;
        }

        this.runCommand(info, ctr, params, cmdArgs, prefix);
    }

    /**
     * runs the command
     *
     * @param info CommandInfo object for get some information about the command
     * @param ctr CommandToRun object for run the command
     * @param params Command parameters from api
     * @param cmdArgs raw command text splitted by spaces and cutted the prefix.
     * @param prefix the current prefix
     */
    protected void runCommand(CommandInfo<T> info, CommandToRun<T> ctr, CProcessParameters<T> params, String[] cmdArgs, String prefix) {
        // async runner first. because async chance is higher than sync commands
        final long firstTime = System.currentTimeMillis();
        cooldownList.put(params.getAuthor().getId(), firstTime);
        if (!info.isSync()) {
            try {
                executorService.submit(() -> {
                    KCommando.logger.info("Last command has been submitted to ExecutorService.");

                    this._internalCaller(ctr, params.getEvent(), cmdArgs, info, prefix);

                    KCommando.logger.info("Last command took " + (System.currentTimeMillis() - firstTime) + "ms to execute.");
                });

            } catch (Throwable t) {
                t.printStackTrace();
            }

        } else {
            this._internalCaller(ctr, params.getEvent(), cmdArgs, info, prefix);

            KCommando.logger.info("Last command took " + (System.currentTimeMillis() - firstTime) + "ms to execute.");
        }
    }

    protected void _internalCaller(CommandToRun<T> ctr, T event, String[] args, CommandInfo<T> info, String prefix) {
        final KRunnable<T> onFalse = info.getOnFalseCallback();

        try {
            Map<String, CommandToRun.MethodToRun> argumentMethods = ctr.getArgumentMethods();

            if (args.length > 1) {
                if (argumentMethods.containsKey(args[1])) {
                    CommandToRun.MethodToRun mtr = argumentMethods.get(args[1]);
                    this.argWrapper(mtr.getType(), mtr.getMethod(), ctr.getClazz(), event, onFalse, args, prefix);
                    return;

                } else if (info.isOnlyArguments()) {
                    if (onFalse != null) {
                        onFalse.run(event);
                    }
                    return;

                }
            }

            this.handleWrapper(ctr.getType(), ctr.getClazz(), event, onFalse, args, prefix);
        } catch (Throwable t) {
            KCommando.logger.info("Command crashed! Message: " + t.getMessage() + "\n" + Arrays.toString(t.getStackTrace()));
        }
    }

    private void handleWrapper(CommandType type, Command<T> clazz,
                               T event, KRunnable<T> onFalse,
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

    private void argWrapper(CommandType type, Method method,
                            Command<T> clazz, T event,
                            KRunnable<T> onFalse, String[] args,
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