package me.koply.kcommando;

import me.koply.kcommando.integration.Integration;

import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

public class KCommando {

    public Parameters params = new Parameters();
    public static final Logger logger = Logger.getLogger("KCommando");
    public static final String VERSION = "4.2.3";

    public KCommando(final Integration integration) {
        params.setIntegration(integration);
    }

    private KInitializer initializer;

    /**
     * only advanced using
     */
    public KCommando(final Integration integration, final KInitializer initializer) {
        params = initializer.getParams();
        params.setIntegration(integration);
        this.initializer = initializer;
    }

    public KCommando build() {
        if (initializer == null) initializer=new KInitializer(params);
        initializer.build();
        return this;
    }

    public KCommando setPackage(String path) { params.setPackagePath(path);
        return this;
    }

    public KCommando setCooldown(long milliseconds) { params.setCooldown(milliseconds);
        return this;
    }

    public KCommando setPrefix(String prefix) { params.setPrefix(prefix);
        return this;
    }

    public KCommando setOwners(String...owners) { params.setOwners(owners);
        return this;
    }

    public KCommando setReadBotMessages(boolean readBotMessages) { params.setReadBotMessages(readBotMessages);
        return this;
    }

    public KCommando setDataFile(File dataFile) {
        params.setDataFile(dataFile);
        return this;
    }

    /**
     * @param dataManager The DataManager instance to be used by kcommando
     * @return this object
     */
    public KCommando setDataManager(DataManager dataManager) {
        params.setDataManager(dataManager);
        return this;
    }

    public KCommando useCaseSensitivity() {
        return useCaseSensitivity(Locale.getDefault());
    }

    public KCommando useCaseSensitivity(Locale locale) {
        params.setCaseSensitivity(locale);
        return this;
    }

    public Parameters getParameters() {
        return params;
    }

    /*
      (k,v): k -> package's name; v -> ${package's display name}&&&${package's description text}
      key example: info
      value example: Information Commands&&&This group haves information commands.
     */
    public KCommando setGroupLocales(Map<String, String> groupLocales) { params.setGroupLocales(groupLocales);
        return this;
        /*
          Still TODO: Pre defined Help command in lib with groupLocales.
         */
    }


}
