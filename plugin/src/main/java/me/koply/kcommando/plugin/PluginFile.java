package me.koply.kcommando.plugin;

import java.io.File;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginFile<E> {
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
    private JavaPlugin<E> instance;

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

    public PluginFile<E> setMainClass(Class<?> mainClass) {
        this.mainClass = mainClass;
        return this;
    }

    public Class<?> getMainClass() {
        return mainClass;
    }

    public PluginFile<E> setInstance(JavaPlugin<E> instance) {
        this.instance = instance;
        return this;
    }

    public JavaPlugin<E> getInstance() {
        return instance;
    }
}