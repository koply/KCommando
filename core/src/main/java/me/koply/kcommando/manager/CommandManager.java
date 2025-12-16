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
    public CommandManager(final KCommando main) {
        this.main = main;
    }

    public final Map<String, CommandBox> commands = new HashMap<>(); // command, commandBox
    public final Map<String, FalseBox> falseBoxMap = new HashMap<>(); // handleFalseMethodName, falseBox
    private SimilarBox similarBox;

    public void setSimilarCallback(final SimilarBox box) {
        this.similarBox = box;
    }

    @Override
    public void registerManager(final KIntegration integration) {
        final CommandHandler.Options options = new CommandHandler.Options(this.main.integration,
                this.main.getPrefix(), this.main.getCooldown(),
                this.main.isUseCaseSensitivity(), this.main.isReadBotMessages(), this.main.isAllowSpacesInPrefix(),
                this.main.getOwnerIds(), this.falseBoxMap, this.main.getDefaultFalseMethodName());
        final CommandHandler handler = new CommandHandler(this.commands, options);
        handler.setSimilarBox(this.similarBox);

        integration.registerCommandHandler(handler);
    }
}