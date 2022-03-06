package me.koply.kcommando.integration.impl.javacord.listeners;

import me.koply.kcommando.handler.CommandHandler;
import me.koply.kcommando.internal.AsyncCaller;
import me.koply.kcommando.internal.CronService;
import me.koply.kcommando.internal.Kogger;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class CommandListener implements MessageCreateListener, AsyncCaller {

    private final CommandHandler handler;
    private final long cooldown;
    public CommandListener(CommandHandler handler) {
        this.handler = handler;
        this.cooldown = handler.options.cooldown;
        CronService.getInstance().addRunnable(() -> cooldownCleaner(cooldown), 2);
    }

    @Override
    public void onMessageCreate(MessageCreateEvent e) {
        if (e.getMessageAuthor().isWebhook()) return;
        if (!handler.options.readBotMessages && e.getMessageAuthor().isBotUser()) return;

        long authorID = e.getMessageAuthor().getId();
        if (authorID == handler.options.integration.selfId) return;

        long ms = System.currentTimeMillis();
        if (ms - cooldownList.getOrDefault(authorID, 0L) < cooldown) return;

        try {
            executorService.submit(() -> {
               boolean result = handler.process(
                       new CommandHandler.Parameters(e, e.getMessageAuthor().getName(),
                               authorID,
                               e.getMessageContent(),
                               e.getServer().isPresent() ? e.getServer().get().getName() : "(PRIVATE)",
                               e.getServer().isPresent() ? e.getServer().get().getId() : -1)
               );
               if (result) cooldownList.put(authorID, ms);
            });
        } catch (Exception ex) {
            Kogger.warn("An error occured while processing a MessageCreateEvent. Stacktrace:");
            ex.printStackTrace();
        }
    }
}