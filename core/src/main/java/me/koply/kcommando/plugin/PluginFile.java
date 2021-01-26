package me.koply.kcommando.plugin;

import java.io.File;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginFile<E, T> {
    public PluginFile(File file, JarFile jarFile, JarEntry pluginEntry, LightYML yml) {
        this.file = file;
        this.jarFile = jarFile;
        this.pluginEntry = pluginEntry;
        this.yml = yml;
    }

    private final File file;
    private final JarFile jarFile;
    private final JarEntry pluginEntry;
    private final LightYML yml;
    private Class<?> mainClass;
    private JavaPlugin<E, T> instance;


    public File getFile() {
        return file;
    }

    public JarFile getJarFile() {
        return jarFile;
    }

    public JarEntry getPluginEntry() {
        return pluginEntry;
    }

    public LightYML getYml() {
        return yml;
    }

    public PluginFile<E, T> setMainClass(Class<?> mainClass) {
        this.mainClass = mainClass;
        return this;
    }

    public Class<?> getMainClass() {
        return mainClass;
    }

    public PluginFile<E, T> setInstance(JavaPlugin<E, T> instance) {
        this.instance = instance;
        return this;
    }

    public JavaPlugin<E, T> getInstance() {
        return instance;
    }
}