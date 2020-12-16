package me.koply.kcommando;

import me.koply.kcommando.integration.Integration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class KCommando {

    public Parameters params = new Parameters();
    public static final Logger logger = Logger.getLogger("KCommando");
    public static final String VERSION = "4.2.0";

    public KCommando(final Integration integration) {
        params.setIntegration(integration);
        setupLogger();
    }

    private KInitializer initializer;

    /*
     * only for advanced using
     */
    public KCommando(final Integration integration, final KInitializer initializer) {
        params = initializer.getParams();
        params.setIntegration(integration);
        this.initializer = initializer;
        setupLogger();
    }

    public KCommando build() {
        if (initializer == null) initializer=new KInitializer(params);
        initializer.build();
        return this;
    }

    private void setupLogger() {
        logger.setUseParentHandlers(false);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new Formatter() {
            private final DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
            @Override
            public String format(LogRecord record) {
                final String[] splitted = record.getSourceClassName().split("\\.");
                final String name = splitted[splitted.length-1];
                return String.format("[%s %s] %s -> %s\n", formatter.format(new Date(record.getMillis())), record.getLevel(), name, record.getMessage());
            }
        });
        logger.addHandler(consoleHandler);
    }

    // setters

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

    public KCommando useCaseSensitivity() {
        return useCaseSensitivity(Locale.getDefault());
    }

    public KCommando useCaseSensitivity(Locale locale) {
        params.setCaseSensitivity(locale);
        return this;
    }

    /*
      (k,v): k -> package's name; v -> ${package's display name}&&&${package's description text}
      key example: info
      value example: Information Commands&&&This group haves information commands.
     */
    public KCommando setGroupLocales(Map<String, String> groupLocales) { params.setGroupLocales(groupLocales);
        return this;
        /*
          TODO: Pre defined Help command in lib with groupLocales.
         */
    }

    public Parameters getParameters() {
        return params;
    }
}
