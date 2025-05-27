package me.koply.kcommando;

import me.koply.kcommando.boot.KInitializer;
import me.koply.kcommando.integration.Integration;
import me.koply.kcommando.internal.Kogger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KCommando {

    public static final String VERSION = "5.0.1";

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
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            Kogger.warn("Initializer field cannot set to the custom initializer class. KCommando will use the default KInitializer.");
            temp = new KInitializer(this);
        }

        this.initializer = temp;
    }

    private final List<String> packagePaths = new ArrayList<>();
    private final Set<Long> ownerIds = new HashSet<>();
    private String prefix;
    private long cooldown;
    private boolean useCaseSensitivity = false;
    private boolean readBotMessages = false;
    public boolean allowSpacesInPrefix = false;
    public static boolean verbose = false;
    private String defaultFalseMethodName = "-";

    public KCommando build() {
        initializer.build();
        return this;
    }

    public void registerObject(Object... customInstances) {
        for (Object customInstance : customInstances) {
            if (verbose) Kogger.info("Registering a custom instance named as " + customInstance.getClass().getName());
            initializer.registerClass(customInstance);
        }
    }

    public List<String> getPackages() {
        return packagePaths;
    }

    public KCommando addPackage(String path) {
        packagePaths.add(path);
        return this;
    }

    public Set<Long> getOwnerIds() {
        return ownerIds;
    }

    public KCommando setOwners(long... ids) {
        for (long id : ids) {
            ownerIds.add(id);
        }
        return this;
    }

    public KCommando clearOwners() {
        ownerIds.clear();
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

    public KCommando setUseCaseSensitivity(boolean caseSensitivity) {
        this.useCaseSensitivity = caseSensitivity;
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

    public String getDefaultFalseMethodName() {
        return defaultFalseMethodName;
    }

    public KCommando setDefaultFalseMethodName(String defaultFalseMethodName) {
        this.defaultFalseMethodName = defaultFalseMethodName;
        return this;
    }
}