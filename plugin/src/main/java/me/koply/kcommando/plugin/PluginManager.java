package me.koply.kcommando.plugin;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @param <E> Listener for JavaPlugin
 */
public class PluginManager<E> {

    private final File folder;
    private final Logger logger;
    public PluginManager(File folder) {
        // kcommando checks the parameter is valid
        this.folder = folder;
        this.logger = getLogger("KCommando Plugin Service");
    }

    private final ArrayList<PluginFile<E>> plugins = new ArrayList<>();
    public final ArrayList<PluginFile<E>> getPlugins() {
        return plugins;
    }

    public void detectPlugins() {
        final ArrayList<URL> urlArray = new ArrayList<>();

        for (File file : folder.listFiles()) {
            if (!file.isFile() && !file.getName().endsWith(".jar")) continue;

            try (JarFile jar = new JarFile(file)) {

                JarEntry jarEntry = jar.getJarEntry("plugin.yml");
                if (jarEntry == null) {
                    logger.warning(file.getName() + "'s plugin.yml file was not found.");
                    continue;
                }

                LightYML yml = new LightYML(jar.getInputStream(jarEntry));
                if (!yml.isOk()) {
                    logger.warning(file.getName() + "'s plugin.yml file has syntax errors.");
                    continue;
                }
                plugins.add(new PluginFile<>(file, jar, jarEntry, yml));
                urlArray.add(file.toURI().toURL());
            } catch (Exception ex) {
                logger.warning("An error occurred while loading the " + file.getName());
                ex.printStackTrace();
            }
        }

        final URL[] urls = new URL[urlArray.size()];
        for (int j = 0; j < urlArray.size(); j++) {
            urls[j] = urlArray.get(j);
        }
        final URLClassLoader loader = new URLClassLoader(urls, this.getClass().getClassLoader());

        for (PluginFile<E> plugin : plugins) {
            try {
                final Class<?> clazz = Class.forName(plugin.getYml().getAttributes().get("main"), true, loader);
                logger.info("Main class successfully found at " + plugin.getYml().getAttributes().get("name"));
                plugin.setMainClass(clazz);
            } catch (ClassNotFoundException classNotFoundException) {
                logger.warning("Main class named as " + plugin.getYml().getAttributes().get("main") + " is not found in the " + plugin.getFile().getName());
            }
        }
    }

    public void enablePlugins() {
        final ArrayList<PluginFile<E>> toremove = new ArrayList<>();
        for (PluginFile<E> plugin : plugins) {
            final String pluginName = plugin.getYml().getAttributes().get("name");

            Class<?> firstSuperClass = plugin.getMainClass().getSuperclass(); // like JDAPlugin and JavacordPlugin
            Class<?> secondSuperClass = firstSuperClass.getSuperclass(); // for JavaPlugin from JDAPlugin
            boolean isOkey = firstSuperClass == JavaPlugin.class || secondSuperClass == JavaPlugin.class;

            if (!isOkey) {
                logger.warning(pluginName + " could not be enabled. Main class is not extends JavaPlugin.");
                toremove.add(plugin);
                continue;
            }

            try {
                final File dataFolder = new File(folder.getPath() + "/" + pluginName + "/");
                //noinspection ResultOfMethodCallIgnored
                dataFolder.mkdir();
                final PluginInfo info = new PluginInfo(dataFolder, pluginName, getLogger(pluginName));
                PluginCargo.setDelivery(info);

                // sorry for this line because we have to do this
                @SuppressWarnings("unchecked")
                JavaPlugin<E> instance = (JavaPlugin<E>) plugin.getMainClass().getDeclaredConstructor().newInstance();
                instance.onEnable();
                logger.info(pluginName + " is enabled!");
                plugin.setInstance(instance);
            } catch (Exception e) {
                logger.warning(pluginName + " could not be enabled.");
                toremove.add(plugin);
            }
        }

        PluginCargo.setDelivery(null);

        for (PluginFile<E> b : toremove) {
            plugins.remove(b);
        }
    }

    private Logger getLogger(String name) {
        final Logger logger = Logger.getLogger(name);
        logger.setUseParentHandlers(false);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new Formatter() {
            private final DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
            public String format(LogRecord record) {
                return String.format("[%s %s] %s -> %s\n", this.formatter.format(new Date(record.getMillis())), record.getLevel(), record.getLoggerName(), record.getMessage());
            }
        });
        logger.addHandler(consoleHandler);

        return logger;
    }
}