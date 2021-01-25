package me.koply.kcommando;

import me.koply.kcommando.internal.Argument;
import me.koply.kcommando.internal.CargoTruck;
import me.koply.kcommando.internal.CommandType;
import me.koply.kcommando.internal.Commando;
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

@SuppressWarnings("rawtypes") // raw use of parameterized class 'Command'
public class KInitializer {

    private final Parameters params;
    public final Parameters getParams() { return params; }
    
    private final Class<? extends CommandHandler> commandHandler;

    public KInitializer(Parameters params) {
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
    public KInitializer(Parameters params, Class<? extends CommandHandler> commandHandler) {
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

        final HashMap<String, CommandToRun> commandMethods = new HashMap<>();
        final Set<Class<? extends Command>> classes = getCommands();

        for (Class<? extends Command> clazz : classes) {
            registerCommand(clazz, commandMethods);
        }
        CargoTruck.setCargo(null);
        params.setCommandMethods(commandMethods);
        try {
            params.getIntegration().register(commandHandler.getDeclaredConstructor(Parameters.class).newInstance(params));
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
        } if (ant.guildOnly() && ant.privateOnly()) {
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
        CommandInfo tempinfo = new CommandInfo();
        tempinfo.initialize(ant);
        CargoTruck.setCargo(tempinfo);
    }

    // sadece bir methodun parametrelerini kontrol ettiğin bir method oluştur ve methodCheck methodunu da
    // oluşturacağın methoda bağla, ayrıca parametre kontrol edeceğin için argRegisterer içinde de
    // o methodu kullanacaksın
    // argüman methodu da boolean dönderecek ve false dönderirse callback çalışacak

    /**
     * a bit hardcoded object type checker
     * org.javacord.api.event.message
     * net.dv8tion.jda.api.events.message
     *
     * @param method Method for check
     * @param checkHandle check for handle name
     * @return found CommandType
     */
    protected CommandType internalMethodCheck(Method method, boolean checkHandle) {
        CommandType type = null;
        if (method.getReturnType() != boolean.class) return null;
        final Class<?>[] parameters = method.getParameterTypes();
        if (!parameters[0].getPackage().getName().contains("message")) return null;
        if (parameters.length <= 3 && (!checkHandle || method.getName().equals("handle"))) {
            if (parameters.length == 1) {
                type = CommandType.EVENT;
            } else if (parameters.length == 2 && parameters[1].isArray()) { // ??
                type = CommandType.ARGNEVENT;
            } else if (parameters.length == 3 && parameters[2] == String.class) {
                type = CommandType.PREFIXED;
            }
        }
        return type;
    }

    /**
     * checks handle methods for type
     *
     * @return if returns null, skips the current class
     */
    protected CommandType methodCheck(Class<? extends Command> clazz) {
        int methodCounter = 0;
        CommandType type = null;
        for (Method method : clazz.getDeclaredMethods()) {
            type = internalMethodCheck(method, true);
            if (type != null) methodCounter++;
        }

        if (methodCounter != 1) {
            KCommando.logger.warning(clazz.getName() + " is have multiple command method. Skipping...");
            return null;
        }
        return type;
    }

    /**
     * checks the argument methods
     * @return argument-MethodToRun objects
     */
    public HashMap<String, CommandToRun.MethodToRun> argRegisterer(final Class<? extends Command> clazz) {
        HashMap<String, CommandToRun.MethodToRun> argumentMethods = new HashMap<>();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            Argument argm = method.getAnnotation(Argument.class);
            if (argm != null) {
                String[] argStr = argm.arg();
                CommandType type = internalMethodCheck(method, false);
                if (type != null) {
                    CommandToRun.MethodToRun mtr = new CommandToRun.MethodToRun(method, type);
                    for (String s : argStr) {
                        argumentMethods.put(s, mtr);
                    }
                }
            }
        }
        return argumentMethods;
    }

    public void registerCommand(final Class<? extends Command> clazz, final HashMap<String, CommandToRun> commandMethods) {
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

            HashMap<String, CommandToRun.MethodToRun> argumentMethods = argRegisterer(clazz);

            final Command commandInstance = clazz.getDeclaredConstructor().newInstance();
            final CommandToRun ctr = new CommandToRun(commandInstance, groupName, type, argumentMethods);

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