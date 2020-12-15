package me.koply.kcommando;

import me.koply.kcommando.internal.CommandType;
import me.koply.kcommando.internal.KRunnable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class CommandHandler {

    private final Parameters params;
    private final ConcurrentHashMap<Long, HashSet<String>> customPrefixes;
    private final Map<String, CommandToRun> commandsMap;
    private final ConcurrentMap<Long, Long> cooldownList = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public CommandHandler(Parameters params) {
        this.params = params;
        commandsMap = params.getCommandMethods();
        customPrefixes = params.getIntegration().getCustomGuildPrefixes();

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

    public void processCommand(final CProcessParameters cpp) {
        final long authorID = cpp.getAuthor().getId();
        if (authorID == params.getSelfUserId() ||
                (!params.isReadBotMessages() && cpp.getAuthor().isBot()) ||
                cpp.isWebhookMessage())
            return;

        final String commandRaw = cpp.getRawCommand();
        final int resultPrefix = checkPrefix(commandRaw, cpp.getGuildID());
        if (resultPrefix == -1) return;
        

        final String[] cmdArgs = commandRaw.substring(resultPrefix).split(" ");
        KCommando.logger.info(String.format("Command received | User: %s | Guild: %s | Command: %s", cpp.getAuthor().getName(), cpp.getGuildName(), commandRaw));
        final String command = params.getCaseSensitivity().isPresent() ? cmdArgs[0] : cmdArgs[0].toLowerCase();

        if (!commandsMap.containsKey(command)) {
            KCommando.logger.info("Last command was not a valid command.");
            return;
        }

        final CommandToRun ctr = commandsMap.get(command);
        final CommandInfo info = ctr.getClazz().getInfo();
        if (info.isGuildOnly() && cpp.getGuildID() == -1) {
            KCommando.logger.info("GuildOnly command used from private channel");
            if (info.getGuildOnlyCallback() != null) {
                executorService.submit(() -> info.getGuildOnlyCallback().run(cpp.getEvent()));
                info.getGuildOnlyCallback().run(cpp.getEvent());
            }
            return;
        }
        if (info.isPrivateOnly() && cpp.getGuildID() != -1) {
            KCommando.logger.info("PrivateOnly command used from guild channel");
            if (info.getPrivateOnlyCallback() != null) {
                executorService.submit(() -> info.getPrivateOnlyCallback().run(cpp.getEvent()));
            }
            return;
        }

        if (info.isOwnerOnly() && !params.getOwners().contains(authorID + "")) {
            KCommando.logger.info("OwnerOnly command used by normal user.");
            if (info.getOwnerOnlyCallback() != null) {
                executorService.submit(() -> info.getOwnerOnlyCallback().run(cpp.getEvent()));
            }
            return;
        }

        if (cooldownCheck(authorID, cooldownList, params.getCooldown()) && !params.getOwners().contains(authorID + "")) {
            KCommando.logger.info("Last command has been declined due to cooldown check");
            if (info.getCooldownCallback() != null) {
                executorService.submit(() -> info.getCooldownCallback().run(cpp.getEvent()));
            }
            return;
        }

        final long firstTime = System.currentTimeMillis();
        cooldownList.put(authorID, firstTime);
        if (info.isSync()) {
            run(ctr, cpp.getEvent(), cmdArgs, info);
            KCommando.logger.info("Last command took " + (System.currentTimeMillis() - firstTime) + "ms to execute.");
        } else {
            KCommando.logger.info("Last command has been submitted to ExecutorService.");
            try {
                executorService.submit(() -> {
                    run(ctr, cpp.getEvent(), cmdArgs, info);
                    KCommando.logger.info("Last command took " + (System.currentTimeMillis() - firstTime) + "ms to execute.");
                });
            } catch (Throwable t) { t.printStackTrace(); }
        }


    }

    private void run(CommandToRun ctr, Object event, String[] args, CommandInfo info) {
        final KRunnable onFalse = info.getOnFalseCallback();
        try {
            if (ctr.getType() == CommandType.ARGNEVENT) {
                if (!ctr.getClazz().handle(event, args) && onFalse != null) {
                    onFalse.run(event);
                }
            } else {
                if (!ctr.getClazz().handle(event) && onFalse != null) {
                    onFalse.run(event);
                }
            }
        }
        catch (Throwable t) { KCommando.logger.info("Command crashed! Message: " + t.getMessage() + "\n" + Arrays.toString(t.getStackTrace())); }
    }

    private boolean cooldownCheck(long userID, ConcurrentMap<Long, Long> cooldownList, long cooldown) {
        long listTime = cooldownList.getOrDefault(userID, 0L);
        return listTime != 0 && System.currentTimeMillis() - listTime <= cooldown;
    }
}