package me.koply.kcommando.boot;

import me.koply.kcommando.KCommando;
import me.koply.kcommando.internal.Kogger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassLooter {

    private ClassLooter() {
    }

    public static Set<Class<?>> getClasses(final List<String> paths) {
        final Set<Class<?>> classes = new HashSet<>();
        paths.forEach(path -> classes.addAll(getClasses(path)));

        if (KCommando.verbose) {
            final String packages = paths.size() > 1 ? String.join("-", paths) : paths.get(0);
            Kogger.info("Given packages are: [" + packages + "]");
            Kogger.info(classes.size() + " class found.");
        }

        return classes;
    }

    /**
     * https://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection
     */
    private static Set<Class<?>> getClasses(final String packageName) {
        final Set<Class<?>> classes = new HashSet<>();

        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        final String path = packageName.replace('.', '/');
        try {
            final Enumeration<URL> resources = classLoader.getResources(path);
            final List<File> dirs = new ArrayList<>();
            while (resources.hasMoreElements()) {
                final URL resource = resources.nextElement();
                dirs.add(new File(resource.getFile()));
            }
            for (final File directory : dirs) {
                classes.addAll(findClasses(directory, packageName));
            }
        } catch (final IOException exception) {
            Kogger.warn("Resources couldn't be read. Package: " + packageName);
        } catch (final ClassNotFoundException exception) {
            Kogger.warn("An error occur while reading a class at the '" + packageName + "' package.");
        }
        return classes;
    }

    /**
     * https://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection
     */
    private static List<Class<?>> findClasses(final File directory, final String packageName) throws ClassNotFoundException {
        final List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        final File[] files = directory.listFiles();
        if (files == null) return classes;

        for (final File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

}