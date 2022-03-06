package me.koply.kcommando;

import me.koply.kcommando.integration.Integration;
import me.koply.kcommando.internal.Kogger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class KCommando {

    public static final String VERSION = "5.0.0";

    public final Integration integration;
    private final KInitializer initializer;
    public KCommando(Integration integration) {
        this.integration = integration;
        this.initializer = new KInitializer(this);
    }

    public KCommando(Integration integration, Class<? extends KInitializer> customInitializer) {
        KInitializer temp;
        this.integration = integration;
        try {
            Constructor<? extends KInitializer> constructor = customInitializer.getDeclaredConstructor(KCommando.class);
            temp = constructor.newInstance(this);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            Kogger.warn("Initializer field cannot set to the custom initializer class. KCommando will use the default KInitializer.");
            temp = new KInitializer(this);
        }

        this.initializer = temp;
    }

    private final List<String> packagePaths = new ArrayList<>();
    private String prefix;
    private long cooldown;
    private boolean useCaseSensitivity;
    private boolean readBotMessages;
    public boolean allowSpacesInPrefix = false;
    public static boolean verbose = false;
    // TODO: data preservence

    public KCommando build() {
        initializer.build();
        return this;
    }

    public void registerCommand(Object...customInstances) {
        for (Object customInstance : customInstances) {
            if (verbose) Kogger.info("Registering a custom instance named as " + customInstance.getClass().getName());
            initializer.registerClass(customInstance);
        }
    }

    public List<String> getPackagePaths() {
        return packagePaths;
    }

    public KCommando addPackagePath(String path) {
        packagePaths.add(path);
        return this;
    }

    public String getPrefix() {
        return prefix;
    }

    public KCommando setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public long getCooldown() {
        return cooldown;
    }

    public KCommando setCooldown(long cooldown) {
        this.cooldown = cooldown;
        return this;
    }

    public boolean isUseCaseSensitivity() {
        return useCaseSensitivity;
    }

    public KCommando setUseCaseSensitivity(boolean useCaseSensitivity) {
        this.useCaseSensitivity = useCaseSensitivity;
        return this;
    }

    public boolean isReadBotMessages() {
        return readBotMessages;
    }

    public KCommando setReadBotMessages(boolean readBotMessages) {
        this.readBotMessages = readBotMessages;
        return this;
    }

    public KCommando setVerbose(boolean verbose) {
        KCommando.verbose = verbose;
        return this;
    }

    public boolean isAllowSpacesInPrefix() {
        return allowSpacesInPrefix;
    }

    public KCommando setAllowSpacesInPrefix(boolean allowSpacesInPrefix) {
        this.allowSpacesInPrefix = allowSpacesInPrefix;
        return this;
    }
}