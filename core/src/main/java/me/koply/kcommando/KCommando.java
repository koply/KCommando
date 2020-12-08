package me.koply.kcommando;

import me.koply.kcommando.integration.Integration;
import me.koply.kcommando.internal.CommandType;
import me.koply.kcommando.internal.Commando;
import org.reflections8.Reflections;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class KCommando {

    private final Parameters params = new Parameters();
    public static final Logger logger = Logger.getLogger("KCommando");
    public static final String VERSION = "4.0-SNAPSHOT";

    public KCommando(final Integration integration) {
        params.setIntegration(integration);
        setupLogger();
    }

    public KCommando build() {
        KCommando.logger.info("KCommando launching!");
        if (params.getIntegration() == null || params.getPackagePath() == null) {
            throw new IllegalArgumentException("We couldn't found integration or commands package path :(");
        }

        final HashMap<String, CommandToRun> commandMethods = new HashMap<>();
        final Reflections reflections = new Reflections(params.getPackagePath());
        final Set<Class<? extends Command>> classes = reflections.getSubTypesOf(Command.class);

        int classCounter = 0;
        for (Class<? extends Command<?>> clazz : classes) {
            final Commando commandAnnotation = clazz.getAnnotation(Commando.class);
            if (commandAnnotation == null) {
                KCommando.logger.warning(clazz.getName() + " is couldn't have Command annotation. Skipping...");
                continue;
            }
            if ((clazz.getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC) {
                KCommando.logger.warning(clazz.getName() + " is not public class. Skipping...");
                continue;
            }
            int methodCounter = 0;
            CommandType type = null;

            if (commandAnnotation.guildOnly() && commandAnnotation.privateOnly()) {
                KCommando.logger.warning(clazz.getName() + " is have GuildOnly and PrivateOnly at the same time. Skipping...");
                continue;
            }

            for (Method method : clazz.getDeclaredMethods()) {
                Class<?>[] parameters = method.getParameterTypes();
                if (parameters.length <= 3 && parameters.length != 0 && method.getName().equals("handle")) {
                    if (parameters.length == 1) {
                        methodCounter++;
                        type = CommandType.EVENT;
                    } else if (parameters[1].isArray()) { // ??
                        methodCounter++;
                        type = CommandType.ARGNEVENT;
                    }
                }
            }

            if (methodCounter > 1) {
                KCommando.logger.warning(clazz.getName() + " is have multiple command method. Skipping...");
                continue;
            }

            final String[] packageSplitted = clazz.getPackage().getName().split("\\.");
            final String groupName = packageSplitted[packageSplitted.length-1];

            try {
                CommandInfo tempinfo = new CommandInfo();
                tempinfo.initialize(commandAnnotation);
                CargoTruck.setCargo(tempinfo);

                final Command commandInstance = clazz.getDeclaredConstructor().newInstance();
                final CommandToRun ctr = new CommandToRun(commandInstance, groupName, type);

                for (final String s : commandAnnotation.aliases()) {
                    final String name = params.getCaseSensitivity().map(s::toLowerCase).orElse(s);
                    commandMethods.put(name, ctr);
                }
                classCounter++;

            } catch (Throwable t) {
                KCommando.logger.warning("Something went wrong.");
            } finally {
                KCommando.logger.info(clazz.getName() + " is have command method");
            }
        }
        CargoTruck.setCargo(null);
        params.setCommandMethods(commandMethods);
        params.getIntegration().register(new CommandHandler(params));
        KCommando.logger.info(classCounter + " commands are initialized.");
        KCommando.logger.info("KCommando system is ready o7");
        return this;
    }

    public static final class CargoTruck {
        private static CommandInfo cargo;
        public static void setCargo(CommandInfo cargo1) {
            cargo = cargo1;
        }
        public static CommandInfo getCargo() {
            return cargo;
        }
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
