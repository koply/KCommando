package me.koply.kcommando.boot;

import me.koply.kcommando.KCommando;
import me.koply.kcommando.internal.Kogger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ClassLooter {

    private ClassLooter() {}

    public static Set<Class<?>> getClasses(List<String> paths) {
        Set<Class<?>> classes = new HashSet<>();
        paths.forEach(path -> classes.addAll(getClasses(path)));

        if (KCommando.verbose) {
            String packages = paths.size() > 1 ? String.join("-", paths) : paths.get(0);
            Kogger.info("Given packages are: [" + packages + "]");
            Kogger.info(classes.size() + " class found.");
        }

        return classes;
    }

    /**
     * https://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection
     */
    private static Set<Class<?>> getClasses(String packageName) {
        Set<Class<?>> classes = new HashSet<>();

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        try {
            Enumeration<URL> resources = classLoader.getResources(path);
            List<File> dirs = new ArrayList<>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                dirs.add(new File(resource.getFile()));
            }
            for (File directory : dirs) {
                classes.addAll(findClasses(directory, packageName));
            }
        } catch (IOException exception) {
            Kogger.warn("Resources couldn't be read. Package: " + packageName);
        } catch (ClassNotFoundException exception) {
            Kogger.warn("An error occur while reading a class at the '" + packageName + "' package.");
        }
        return classes;
    }

    /**
     * https://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection
     */
    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        if (files == null) return classes;

        for (File file : files) {
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