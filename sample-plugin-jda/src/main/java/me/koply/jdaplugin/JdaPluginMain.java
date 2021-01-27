package me.koply.jdaplugin;

import me.koply.kcommando.integration.impl.jda.JDAPlugin;

public class JdaPluginMain extends JDAPlugin {

    public JdaPluginMain() {
        getLogger().info("Hello from JdaPluginMain constructor.");
    }

    @Override
    public void onEnable() {
        getLogger().info("Hello from JdaPluginMain onEnable.");

        addCommand(JdaPluginCommand.class);
    }

    @Override
    public void onDisable() {
    }
}