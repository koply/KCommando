package me.koply.kcommando;

import me.koply.kcommando.annotations.Command;
import me.koply.kcommando.exceptions.NotEnoughData;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.reflections8.Reflections;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

// signed by Koply
public final class KCommando {

    private final Params params = new Params();
    public static final Logger logger = Logger.getLogger("KCommando");
    public static final String VERSION = "2.4";

    public KCommando(@NotNull JDA jda) {
        params.setJda(jda);
        setupLogger();
    }

    public KCommando build() {
        KCommando.logger.info("KCommando launching!");
        if (params.getJda() == null || params.getPackagePath() == null) {
            throw new NotEnoughData("We couldn't found JDA or commands package path :(");
        }

        HashMap<String, CommandToRun> commandMethods = new HashMap<>();
        Reflections reflections = new Reflections(params.getPackagePath());

        Set<Class<? extends CommandUtils>> classes = reflections.getSubTypesOf(CommandUtils.class);
        int classCounter = 0;
        for (Class<? extends CommandUtils> clazz : classes) {
            Command cmdAnnotation = clazz.getAnnotation(Command.class);
            if (cmdAnnotation == null) {
                KCommando.logger.warning(clazz.getName() + " is couldn't have Command annotation. Skipping...");
                continue;
            }
            if ((clazz.getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC) {
                KCommando.logger.warning(clazz.getName() + " is not public class. Skipping...");
                continue;
            }
            int methodCounter = 0;
            CommandUtils.TYPE type = null;

            if (cmdAnnotation.guildOnly() && cmdAnnotation.privateOnly()) {
                KCommando.logger.warning(clazz.getName() + " is have GuildOnly and PrivateOnly at the same time. Skipping...");
                continue;
            }

            for (Method metod : clazz.getDeclaredMethods()) {
                Class<?>[] parameters = metod.getParameterTypes();
                if (parameters.length <= 3 && parameters.length != 0 && metod.getName().equals("handle")) {
                    if (parameters.length == 1) {
                        if (parameters[0] == MessageReceivedEvent.class) {
                            methodCounter++;
                            type = CommandUtils.TYPE.EVENT;
                        }
                    } else if (parameters[0] == MessageReceivedEvent.class && parameters[1] == Params.class) {
                        methodCounter++;
                        type = CommandUtils.TYPE.PARAMETEREDEVENT;
                    } else if (parameters[0] == MessageReceivedEvent.class && parameters[1].isArray()) { // ??
                        methodCounter++;
                        type = CommandUtils.TYPE.ARGNEVENT;
                    }
                }
            }

            if (methodCounter > 1) {
                KCommando.logger.warning(clazz.getName() + " is have multiple command method. Skipping...");
                continue;
            }

            String[] packageSplitted = clazz.getPackage().getName().split("\\.");
            String groupName = packageSplitted[packageSplitted.length-1];

            try {
                CommandToRun ctr = new CommandToRun()
                        .setClazz(clazz.newInstance())
                        .setCommandAnnotation(cmdAnnotation)
                        .setType(type)
                        .setGroupName(groupName);

                for (String s : cmdAnnotation.names()) {
                    commandMethods.put(s, ctr);
                }
                classCounter++;

            } catch (Throwable t) {
                KCommando.logger.warning("Something went wrong.");
            } finally {
                KCommando.logger.info(clazz.getName() + " is have command method");
            }

        }
        params.setCommandMethods(commandMethods);
        params.getJda().addEventListener(new CommandHandler(params));
        KCommando.logger.info(classCounter + " commands are initialized.");
        KCommando.logger.info("KCommando system is ready o7");
        return this;
    }

    private void setupLogger() {
        logger.setUseParentHandlers(false);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new Formatter() {
            private final DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
            @Override
            public String format(LogRecord record) {
                // [22/04/2020 18:01:30.533 INFO] Message
                return String.format("[%s %s] %s\n", formatter.format(new Date(record.getMillis())), record.getLevel(), record.getMessage());
            }
        });
        logger.addHandler(consoleHandler);
    }

    // setters

    public KCommando setPackage(@NotNull String path) { params.setPackagePath(path);
        return this;
    }

    public KCommando setCooldown(long milliseconds) { params.setCooldown(milliseconds);
        return this;
    }

    public KCommando setPrefix(@NotNull String prefix) { params.setPrefix(prefix);
        return this;
    }

    public KCommando setOwners(@NotNull String...owners) { params.setOwners(owners);
        return this;
    }

    public KCommando setReadBotMessages(boolean readBotMessages) { params.setReadBotMessages(readBotMessages);
        return this;
    }

    /*
      (k,v): k -> package's name; v -> ${package's display name}&&&${package's description text}
      key example: info
      value example: Information Commands&&&This group haves information commands.
     */
    public KCommando setGroupLocales(@NotNull HashMap<String, String> groupLocales) { params.setGroupLocales(groupLocales);
        return this;
        /*
          TODO: Pre defined Help command in lib with groupLocales.
         */
    }

    public Params getParams() {
        return params;
    }
}
