package me.koply.kcommando.manager;

import me.koply.kcommando.integration.KIntegration;

public abstract class Manager {
    public abstract void registerManager(KIntegration integration);
}