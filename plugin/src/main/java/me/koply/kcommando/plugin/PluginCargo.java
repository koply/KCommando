package me.koply.kcommando.plugin;

public class PluginCargo {
    private static PluginInfo delivery;
    public static PluginInfo getDelivery() {
        return delivery;
    }
    public static void setDelivery(PluginInfo info) {
        delivery = info;
    }
}