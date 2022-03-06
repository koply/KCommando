package me.koply.kcommando.manager;

import me.koply.kcommando.KCommando;
import me.koply.kcommando.handler.CommandHandler;
import me.koply.kcommando.integration.KIntegration;
import me.koply.kcommando.internal.boxes.CommandBox;
import me.koply.kcommando.internal.boxes.SimilarBox;

import java.util.HashMap;
import java.util.Map;

public class CommandManager extends Manager {

    private final CommandHandler handler;
    public CommandManager(KCommando main) {
        CommandHandler.Options options = new CommandHandler.Options(main.integration,
                main.getPrefix(), main.getCooldown(),
                main.isUseCaseSensitivity(), main.isReadBotMessages(), main.isAllowSpacesInPrefix());
        this.handler = new CommandHandler(commands, options);
    }

    public final Map<String, CommandBox> commands = new HashMap<>();

    public void setSimilarCallback(SimilarBox box) {
        handler.setSimilarBox(box);
    }

    @Override
    public void registerManager(KIntegration integration) {
        integration.registerCommandHandler(handler);
    }
}