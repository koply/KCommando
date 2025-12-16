package me.koply.kcommando.manager;

import me.koply.kcommando.handler.ButtonClickHandler;
import me.koply.kcommando.integration.KIntegration;
import me.koply.kcommando.internal.boxes.ButtonBox;

import java.util.HashMap;
import java.util.Map;

public class ButtonManager extends Manager {

    private final ButtonClickHandler handler;
    public ButtonManager() {
        this.handler = new ButtonClickHandler(this.buttons);
    }

    public final Map<String, ButtonBox> buttons = new HashMap<>();

    @Override
    public void registerManager(final KIntegration integration) {
        integration.registerButtonClickHandler(this.handler);
    }
}