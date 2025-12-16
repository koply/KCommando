package me.koply.kcommando.manager;

import me.koply.kcommando.handler.SlashCommandHandler;
import me.koply.kcommando.integration.KIntegration;
import me.koply.kcommando.internal.boxes.SlashBox;

import java.util.HashMap;
import java.util.Map;

public class SlashManager extends Manager {

    private final SlashCommandHandler handler;
    public SlashManager() {
        this.handler = new SlashCommandHandler(this.commands);
    }

    public final Map<String, SlashBox> commands = new HashMap<>();

    @Override
    public void registerManager(final KIntegration integration) {
        integration.registerSlashCommandHandler(this.handler);
    }
}