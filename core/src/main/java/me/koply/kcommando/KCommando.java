package me.koply.kcommando;

import me.koply.kcommando.boot.KInitializer;
import me.koply.kcommando.integration.Integration;
import me.koply.kcommando.internal.Kogger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KCommando {

    public static final String VERSION = "5.0.1";

    public final Integration integration;
    private final KInitializer initializer;

    public KCommando(final Integration integration) {
        this.integration = integration;
        this.initializer = new KInitializer(this);
    }

    public KCommando(final Integration integration, final Class<? extends KInitializer> customInitializer) {
        KInitializer temp;
        this.integration = integration;
        try {
            final Constructor<? extends KInitializer> constructor = customInitializer.getDeclaredConstructor(KCommando.class);
            temp = constructor.newInstance(this);
        } catch (final NoSuchMethodException | InvocationTargetException | InstantiationException |
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
        this.initializer.build();
        return this;
    }

    public void registerObject(final Object... customInstances) {
        for (final Object customInstance : customInstances) {
            if (verbose) Kogger.info("Registering a custom instance named as " + customInstance.getClass().getName());
            this.initializer.registerClass(customInstance);
        }
    }

    public List<String> getPackages() {
        return Collections.unmodifiableList(this.packagePaths);
    }

    public KCommando addPackage(final String path) {
        this.packagePaths.add(path);
        return this;
    }

    public Set<Long> getOwnerIds() {
        return Collections.unmodifiableSet(this.ownerIds);
    }

    public KCommando setOwners(final long... ids) {
        for (final long id : ids) {
            this.ownerIds.add(id);
        }
        return this;
    }

    public KCommando clearOwners() {
        this.ownerIds.clear();
        return this;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public KCommando setPrefix(final String prefix) {
        this.prefix = prefix;
        return this;
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public KCommando setCooldown(final long cooldown) {
        this.cooldown = cooldown;
        return this;
    }

    public boolean isUseCaseSensitivity() {
        return this.useCaseSensitivity;
    }

    public KCommando setUseCaseSensitivity(final boolean caseSensitivity) {
        this.useCaseSensitivity = caseSensitivity;
        return this;
    }

    public boolean isReadBotMessages() {
        return this.readBotMessages;
    }

    public KCommando setReadBotMessages(final boolean readBotMessages) {
        this.readBotMessages = readBotMessages;
        return this;
    }

    public static void setVerbose(final boolean verbose) {
        KCommando.verbose = verbose;
    }

    public boolean isAllowSpacesInPrefix() {
        return this.allowSpacesInPrefix;
    }

    public KCommando setAllowSpacesInPrefix(final boolean allowSpacesInPrefix) {
        this.allowSpacesInPrefix = allowSpacesInPrefix;
        return this;
    }

    public String getDefaultFalseMethodName() {
        return this.defaultFalseMethodName;
    }

    public KCommando setDefaultFalseMethodName(final String defaultFalseMethodName) {
        this.defaultFalseMethodName = defaultFalseMethodName;
        return this;
    }
}