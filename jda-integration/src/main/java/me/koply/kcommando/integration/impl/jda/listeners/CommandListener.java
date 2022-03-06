package me.koply.kcommando.integration.impl.jda.listeners;

import me.koply.kcommando.handler.CommandHandler;
import me.koply.kcommando.internal.AsyncCaller;
import me.koply.kcommando.internal.CronService;
import me.koply.kcommando.internal.Kogger;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class CommandListener extends ListenerAdapter implements AsyncCaller {

    private final CommandHandler handler;
    private final long cooldown;
    public CommandListener(CommandHandler handler) {
        this.handler = handler;
        this.cooldown = handler.options.cooldown;
        CronService.getInstance().addRunnable(() -> cooldownCleaner(cooldown), 2);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
        if (e.isWebhookMessage()) return;
        if (!handler.options.readBotMessages && e.getAuthor().isBot()) return;

        long authorID = e.getAuthor().getIdLong();
        if (authorID == handler.options.integration.selfId) return;

        long ms = System.currentTimeMillis();
        if (ms - cooldownList.getOrDefault(authorID, 0L) < cooldown) return;

        try {
            executorService.submit(() -> {
                boolean result = handler.process(
                        new CommandHandler.Parameters(e, e.getAuthor().getName(),
                                authorID,
                                e.getMessage().getContentRaw(),
                                e.isFromGuild() ? e.getGuild().getName() : "(PRIVATE)",
                                e.isFromGuild() ? e.getGuild().getIdLong() : -1)
                );
                if (result) cooldownList.put(authorID, ms);
            });
        } catch (Exception ex) {
            Kogger.warn("An error occured while processing a MessageReceivedEvent. Stacktrace:");
            ex.printStackTrace();
        }
    }
}