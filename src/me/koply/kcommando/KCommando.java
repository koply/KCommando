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

public final class KCommando {

    private final Params params = new Params();
    public static final Logger logger = Logger.getLogger("KCommando");

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
        for (Class<?> clazz : classes) {
            int methodCounter = 0;
            for (Method metod : clazz.getMethods()) {
                if (metod.getAnnotation(Command.class) == null || metod.getParameterTypes()[0] != MessageReceivedEvent.class && metod.getParameterTypes()[1] != Params.class) {
                    continue;
                }
                Command cmdAnnotation = metod.getAnnotation(Command.class);
                if (cmdAnnotation.guildOnly() && cmdAnnotation.privateOnly()) {
                    KCommando.logger.info(clazz.getName()+"#"+metod.getName() + " is have GuildOnly and PrivateOnly at the same time. Skipping...");
                    continue;
                }

                methodCounter++;

                String[] packageSplitted = clazz.getPackage().getName().split("\\.");
                String groupName = packageSplitted[packageSplitted.length-1];

                Class<?> cino = clazz;

                if ((metod.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
                    cino = null;
                }

                CommandToRun ctr = new CommandToRun()
                        .setKlass(cino)
                        .setMethod(metod)
                        .setCommandAnnotation(cmdAnnotation)
                        .setGroupName(groupName);

                commandMethods.put(cmdAnnotation.names()[0], ctr);
            }
            KCommando.logger.info(clazz.getName() + " is have " + methodCounter + " command method");
        }
        params.setCommandMethods(commandMethods);
        params.getJda().addEventListener(new CommandHandler(params));
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
