package me.koply.kcommando;

import me.koply.kcommando.integration.Integration;

import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

public class KCommando<T> {

    public Parameters<T> params = new Parameters<>();
    public static final Logger logger = Logger.getLogger("KCommando");
    public static final String VERSION = "4.2.5";

    public KCommando(final Integration<T> integration) {
        params.setIntegration(integration);
    }

    private KInitializer<T> initializer;

    /**
     * only advanced using
     */
    public KCommando(final Integration<T> integration, final KInitializer<T> initializer) {
        params = initializer.getParams();
        params.setIntegration(integration);
        this.initializer = initializer;
    }

    public KCommando<T> build() {
        if (initializer == null) initializer = new KInitializer<>(params);
        initializer.build();
        return this;
    }

    public KCommando<T> setPackage(String path) { params.setPackagePath(path);
        return this;
    }

    public KCommando<T> setCooldown(long milliseconds) { params.setCooldown(milliseconds);
        return this;
    }

    public KCommando<T> setPrefix(String prefix) { params.setPrefix(prefix);
        return this;
    }

    public KCommando<T> setOwners(String...owners) { params.setOwners(owners);
        return this;
    }

    public KCommando<T> setReadBotMessages(boolean readBotMessages) { params.setReadBotMessages(readBotMessages);
        return this;
    }

    public KCommando<T> setDataFile(File dataFile) {
        params.setDataFile(dataFile);
        return this;
    }

    public KCommando<T> setPluginsPath(File pluginsPath) {
        params.setPluginsPath(pluginsPath);
        return this;
    }

    /**
     * @param dataManager The DataManager instance to be used by kcommando
     * @return this object
     */
    public KCommando<T> setDataManager(DataManager<T> dataManager) {
        params.setDataManager(dataManager);
        return this;
    }

    public KCommando<T> useCaseSensitivity() {
        return useCaseSensitivity(Locale.getDefault());
    }

    public KCommando<T> useCaseSensitivity(Locale locale) {
        params.setCaseSensitivity(locale);
        return this;
    }

    public Parameters<T> getParameters() {
        return params;
    }

    /*
      (k,v): k -> package's name; v -> ${package's display name}&&&${package's description text}
      key example: info
      value example: Information Commands&&&This group haves information commands.
     */
    public KCommando<T> setGroupLocales(Map<String, String> groupLocales) { params.setGroupLocales(groupLocales);
        return this;
        /*
          Still TODO: Pre defined Help command in lib with groupLocales.
         */
    }


}
