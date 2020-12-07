package me.koply.kcommando;

import me.koply.kcommando.enums.CommandType;
import me.koply.kcommando.internal.KRunnable;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class CommandHandler extends ListenerAdapter {

    private final Params params;
    private final HashMap<String, CommandToRun> commandsMap;
    private final ConcurrentMap<Long, Long> cooldownList = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public CommandHandler(Params params) {
        this.params = params;
        commandsMap = params.getCommandMethods();

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

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
        if (e.getAuthor().getId().equals(params.getJda().getSelfUser().getId())) return;
        if (!params.isReadBotMessages() && e.getAuthor().isBot()) return;
        if (e.isWebhookMessage()) return;

        final String commandRaw = e.getMessage().getContentRaw();
        if (!commandRaw.startsWith(params.getPrefix())) return;

        final String guildName = e.isFromGuild() ? e.getGuild().getName() : "(PRIVATE)";

        final String[] cmdArgs = commandRaw.substring(params.getPrefix().length()).split(" ");
        KCommando.logger.info(String.format("Command received | User: %s | Guild: %s | Command: %s", e.getAuthor().getAsTag(), guildName, commandRaw));

        final String command = params.getCaseSensitivity().isPresent() ? cmdArgs[0] : cmdArgs[0].toLowerCase();

        if (!commandsMap.containsKey(command)) {
            KCommando.logger.info("Last command was not a valid command.");
            return;
        }

        final CommandToRun ctr = commandsMap.get(command);
        final CommandInfo info = ctr.getClazz().getInfo();
        if (info.isGuildOnly() && !e.isFromGuild()) {
            KCommando.logger.info("GuildOnly command used from private channel");
            if (info.getGuildOnlyCallback() != null) {
                executorService.submit(() -> info.getGuildOnlyCallback().run(e));
                info.getGuildOnlyCallback().run(e);
            }
            return;
        }
        if (info.isPrivateOnly() && e.isFromGuild()) {
            KCommando.logger.info("PrivateOnly command used from guild channel");
            if (info.getPrivateOnlyCallback() != null) {
                executorService.submit(() -> info.getPrivateOnlyCallback().run(e));
            }
            return;
        }

        long authorID = e.getAuthor().getIdLong();
        if (info.isOwnerOnly() && !params.getOwners().contains(authorID + "")) {
            KCommando.logger.info("OwnerOnly command used by normal user.");
            if (info.getOwnerOnlyCallback() != null) {
                executorService.submit(() -> info.getOwnerOnlyCallback().run(e));
            }
            return;
        }

        if (cooldownCheck(authorID, cooldownList, params.getCooldown()) && !params.getOwners().contains(authorID + "")) {
            KCommando.logger.info("Last command has been declined due to cooldown check");
            if (info.getCooldownCallback() != null) {
                executorService.submit(() -> info.getCooldownCallback().run(e));
            }
            return;
        }

        long firstTime = System.currentTimeMillis();
        cooldownList.put(authorID, firstTime);
        if (info.isSync()) {
            run(ctr, e, cmdArgs, info);
            KCommando.logger.info("Last command took " + (System.currentTimeMillis() - firstTime) + "ms to execute.");
        } else {
            KCommando.logger.info("Last command has been submitted to ExecutorService.");
            try {
                executorService.submit(() -> {
                    run(ctr, e, cmdArgs, info);
                    KCommando.logger.info("Last command took " + (System.currentTimeMillis() - firstTime) + "ms to execute.");
                });
            } catch (Throwable t) { t.printStackTrace(); }
        }
    }

    private void run(CommandToRun ctr, MessageReceivedEvent e, String[] args, CommandInfo info) {
        KRunnable onFalse = info.getOnFalseCallback();
        try {
            if (ctr.getType() == CommandType.ARGNEVENT) {
                if (!ctr.getClazz().handle(e, args) && onFalse != null) {
                    onFalse.run(e);
                }
            } else {
                if (!ctr.getClazz().handle(e) && onFalse != null) {
                    onFalse.run(e);
                }
            }
        }
        catch (Throwable t) { KCommando.logger.info("Command crashed! Message: " + Arrays.toString(t.getStackTrace())); }
    }

    private boolean cooldownCheck(long userID, ConcurrentMap<Long, Long> cooldownList, long cooldown) {
        long listTime = cooldownList.getOrDefault(userID, 0L);
        return listTime != 0 && System.currentTimeMillis() - listTime <= cooldown;
    }
}