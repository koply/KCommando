package me.koply.kcommando.manager;

import me.koply.kcommando.KCommando;
import me.koply.kcommando.handler.CommandHandler;
import me.koply.kcommando.integration.KIntegration;
import me.koply.kcommando.internal.boxes.CommandBox;
import me.koply.kcommando.internal.boxes.FalseBox;
import me.koply.kcommando.internal.boxes.SimilarBox;

import java.util.HashMap;
import java.util.Map;

public class CommandManager extends Manager {

    private final KCommando main;
    public CommandManager(KCommando main) {
        this.main = main;
    }

    public final Map<String, CommandBox> commands = new HashMap<>(); // command, commandBox
    public final Map<String, FalseBox> falseBoxMap = new HashMap<>(); // handleFalseMethodName, falseBox
    private SimilarBox similarBox;

    public void setSimilarCallback(SimilarBox box) {
        this.similarBox = box;
    }

    @Override
    public void registerManager(KIntegration integration) {
        CommandHandler.Options options = new CommandHandler.Options(main.integration,
                main.getPrefix(), main.getCooldown(),
                main.isUseCaseSensitivity(), main.isReadBotMessages(), main.isAllowSpacesInPrefix(),
                main.getOwnerIds(), falseBoxMap, main.getDefaultFalseMethodName());
        CommandHandler handler = new CommandHandler(commands, options);
        handler.setSimilarBox(similarBox);

        integration.registerCommandHandler(handler);
    }
}