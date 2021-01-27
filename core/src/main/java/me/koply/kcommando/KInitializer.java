package me.koply.kcommando;

import me.koply.kcommando.internal.CargoTruck;
import me.koply.kcommando.internal.Command;
import me.koply.kcommando.internal.CommandInfo;
import me.koply.kcommando.internal.CommandType;
import me.koply.kcommando.internal.annotations.Argument;
import me.koply.kcommando.internal.annotations.Commando;
import org.reflections8.Reflections;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class KInitializer<T> {

    private final Parameters<T> params;
    public final Parameters<T> getParams() { return params; }
    
    private final Class<? extends CommandHandler> commandHandler;

    public KInitializer(Parameters<T> params) {
        this.params = params;
        this.commandHandler = CommandHandler.class;
        setupLogger();
    }

    /**
     * this constructor for advanced usage
     *
     * @param params parameters for the runs bot
     * @param commandHandler custom commandHandler *class* for the kcommando
     */
    public KInitializer(Parameters<T> params, Class<? extends CommandHandler> commandHandler) {
        this.params = params;
        this.commandHandler = commandHandler;
        setupLogger();
    }

    /**
     * initializes the logger
     */
    public void setupLogger() {
        KCommando.logger.setUseParentHandlers(false);

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

        KCommando.logger.addHandler(consoleHandler);
    }

    /**
     * @return command classes
     */
    public Set<Class<? extends Command>> getCommands() {
        final Reflections reflections = new Reflections(params.getPackagePath());
        return reflections.getSubTypesOf(Command.class);
    }

    public Set<Class<? extends Command>> enableAndGetCommands() {
        params.getIntegration().detectAndEnablePlugins(params);
        return params.getIntegration().getPluginCommands();
    }

    private int classCounter = 0;
    /**
     * Classic build pattern. Register's CommandHandler and uses reflections from getCommands method.
     */
    public void build() {
        KCommando.logger.info("KCommando launching!");
        if (params.getIntegration() == null || params.getPackagePath() == null) {
            throw new IllegalArgumentException("We couldn't found integration or commands package path :(");
        }

        params.getDataManager().ifPresent(DataManager::initDataFile);

        final Map<String, CommandToRun<T>> commandMethods = new HashMap<>();
        final Set<Class<? extends Command>> classes = getCommands();

        boolean pluginSystem = params.getPluginsPath() != null;

        if (pluginSystem) {
            Set<Class<? extends Command>> pluginClasses = enableAndGetCommands();
            if (!pluginClasses.isEmpty()) {
                KCommando.logger.info(pluginClasses.size() + " command found from plugins.");
                classes.addAll(pluginClasses);
            }
        }

        for (Class<? extends Command> clazz : classes) {
            registerCommand(clazz, commandMethods);
        }

        CargoTruck.setCargo(null);
        params.setCommandMethods(commandMethods);

        try {
            params.getIntegration().registerCommandHandler(commandHandler.getDeclaredConstructor(Parameters.class).newInstance(params));

            if (pluginSystem) params.getIntegration().registerListeners();
        } catch (Exception ex) {
            ex.printStackTrace();
            KCommando.logger.info("An unexpected error caught at initialize the command handler.");
        }

        KCommando.logger.info(classCounter + " commands are initialized.");
        KCommando.logger.info("KCommando system is ready o7");
    }

    /**
     * @return if returns true skips class
     */
    private boolean preCheck(Class<? extends Command> clazz) {
        if (clazz.getPackage().getName().contains("me.koply.kcommando.integration.impl")) return true;

        if ((clazz.getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC) {
            KCommando.logger.warning(clazz.getName() + " is not public class. Skipping...");
            return true;
        }

        return false;
    }

    /**
     * @return if returns true skips class
     */
    private boolean annotationCheck(Commando ant, String clazzName) {
        if (ant == null) {
            KCommando.logger.warning(clazzName + " is couldn't have Commando annotation. Skipping...");
            return true;
        }

        if (ant.guildOnly() && ant.privateOnly()) {
            KCommando.logger.warning(clazzName + " has GuildOnly and PrivateOnly at the same time. Skipping...");
            return true;
        }

        return false;
    }

    /**
     * creates info and pushes to cargo
     *
     * @param ant the command annotation
     */
    public void infoGenerator(Commando ant) {
        CommandInfo<T> tempinfo = new CommandInfo<>();
        tempinfo.initialize(ant);
        CargoTruck.setCargo(tempinfo);
    }

    /**
     * a bit hardcoded object type checker
     * org.javacord.api.event.message
     * net.dv8tion.jda.api.events.message
     *
     * @param method Method for check
     * @param checkHandle check for handle name
     * @return found CommandType
     */
    protected CommandType _internalMethodCheck(Method method, boolean checkHandle) {
        if (method.getReturnType() != boolean.class) return null;

        final Class<?>[] parameters = method.getParameterTypes();
        if (!parameters[0].getPackage().getName().contains("message")) return null;

        boolean isOk = !checkHandle || method.getName().equals("handle");

        if (parameters.length <= 3 && isOk) {
            if (parameters.length == 1) {
                return CommandType.EVENT;

            } else if (parameters.length == 2 && parameters[1].isArray()) { // ??
                return CommandType.ARGNEVENT;

            } else if (parameters.length == 3 && parameters[2] == String.class) {
                return CommandType.PREFIXED;

            }
        }

        return null;
    }

    /**
     * checks handle methods for type
     *
     * @return if returns null, skips the current class
     */
    protected CommandType methodCheck(Class<? extends Command> clazz) {
        CommandType type = null;

        for (Method method : clazz.getDeclaredMethods()) {
            type = _internalMethodCheck(method, true);
            if (type != null) break;
        }

        return type;
    }

    /**
     * checks the argument methods
     * @return argument-MethodToRun objects
     */
    public Map<String, CommandToRun.MethodToRun> argRegisterer(final Class<? extends Command> clazz) {
        Map<String, CommandToRun.MethodToRun> argumentMethods = new HashMap<>();

        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            if ((method.getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC) continue;

            Argument argm = method.getAnnotation(Argument.class);
            if (argm == null) continue;

            CommandType type = _internalMethodCheck(method, false);
            if (type == null) continue;

            String[] argStr = argm.arg();
            CommandToRun.MethodToRun mtr = new CommandToRun.MethodToRun(method, type);
            for (String s : argStr) {
                argumentMethods.put(s, mtr);
            }
        }
        return argumentMethods;
    }

    public void registerCommand(final Class<? extends Command> clazz, final Map<String, CommandToRun<T>> commandMethods) {
        // for package and class public modifier
        if (preCheck(clazz)) return;

        final Commando commandAnnotation = clazz.getAnnotation(Commando.class);
        if (annotationCheck(commandAnnotation, clazz.getName())) return;

        final CommandType type = methodCheck(clazz);
        if (type == null) return;

        final String[] packageSplitted = clazz.getPackage().getName().split("\\.");
        final String groupName = packageSplitted[packageSplitted.length-1];

        try {
            infoGenerator(commandAnnotation);
            Map<String, CommandToRun.MethodToRun> argumentMethods = argRegisterer(clazz);

            @SuppressWarnings("unchecked")
            final Command<T> commandInstance = clazz.getDeclaredConstructor().newInstance();
            final CommandToRun<T> ctr = new CommandToRun<>(commandInstance, groupName, type, argumentMethods);

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
}