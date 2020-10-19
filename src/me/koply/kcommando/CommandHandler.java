package me.koply.kcommando;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class CommandHandler extends ListenerAdapter {

    private final Params params;
    private final HashMap<String, CommandToRun> commandsMap;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public CommandHandler(Params params) {
        this.params = params;
        commandsMap = params.getCommandMethods();
        KCommando.logger.info("[KCommando] CommandHandler initialized.");
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
        if (e.getAuthor().getId().equals(params.getJda().getSelfUser().getId())) {
            return;
        }

        if (!params.isReadBotMessages() && e.getAuthor().isBot()) {
            return;
        }

        if (e.isWebhookMessage()) {
            return;
        }

        String commandRaw = e.getMessage().getContentRaw();
        if (!commandRaw.startsWith(params.getPrefix())) {
            return;
        }

        String guildName;
        if (e.isFromGuild()) {
            guildName = e.getGuild().getName();
        } else {
            guildName = "(PRIVATE)";
        }

        String[] cmdArgs = commandRaw.substring(params.getPrefix().length()).split(" ");
        KCommando.logger.info(String.format("Command received | User: %s | Guild: %s | Command: %s", e.getAuthor().getAsTag(), guildName, commandRaw));

        if (!commandsMap.containsKey(cmdArgs[0])) {
            KCommando.logger.info("Last command was not a valid command.");
            return;
        }

        CommandToRun ctr = commandsMap.get(cmdArgs[0]);
        if (ctr.getCommandAnnotation().guildOnly() && !e.isFromGuild()) {
            KCommando.logger.info("GuildOnly command used from private channel");
            return;
        }
        if (ctr.getCommandAnnotation().privateOnly() && e.isFromGuild()) {
            KCommando.logger.info("PrivateOnly command used from guild channel");
            return;
        }

        String authorID = e.getAuthor().getId();
        if (ctr.getCommandAnnotation().ownerOnly() && !params.getOwners().contains(authorID)) {
            KCommando.logger.info("OwnerOnly command used by normal user.");
            return;
        }

        if (cooldownCheck(authorID, params.getCooldownList(), params.getCooldown()) && !params.getOwners().contains(authorID)) {
            KCommando.logger.info("Last command has been declined due to cooldown check");
            return;
        }


        long firstTime = System.currentTimeMillis();
        params.getCooldownList().put(authorID, firstTime);
        if (ctr.getCommandAnnotation().sync()) {
            try { ctr.getMethod().invoke(ctr.getKlass(), e);}
            catch (Throwable t) { KCommando.logger.info("Command crashed! Message: " + t.getMessage()); }

        } else {
            KCommando.logger.info("Last command has been submitted to ExecutorService.");
            try {
                executorService.submit(() -> {
                    try { ctr.getMethod().invoke(ctr.getKlass().newInstance(), e);}
                    catch (Throwable t) { KCommando.logger.info("Command crashed! Message: " + t.getMessage()); }
                });
            } catch (Throwable t) { t.printStackTrace(); }
        }
        KCommando.logger.info("Last command took " + (System.currentTimeMillis() - firstTime) + "ms to execute.");
    }

    private boolean cooldownCheck(String userID, ConcurrentMap<String, Long> cooldownList, long cooldown) {
        long listTime = cooldownList.getOrDefault(userID, 0L);
        if (listTime == 0) return false;
        else return System.currentTimeMillis() - listTime <= cooldown;
    }
}