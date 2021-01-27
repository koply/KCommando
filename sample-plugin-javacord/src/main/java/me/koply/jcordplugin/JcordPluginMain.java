package me.koply.jcordplugin;

import me.koply.kcommando.integration.impl.javacord.JavacordPlugin;

public class JcordPluginMain extends JavacordPlugin {

    public JcordPluginMain() {
        getLogger().info("Hello from JcordPluginMain constructor.");
    }

    @Override
    public void onEnable() {
        getLogger().info("Hello from JcordPluginMain onEnable.");

        addCommand(JcordPluginCommand.class);
    }

    @Override
    public void onDisable() {
    }
}