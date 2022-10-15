package me.koply.kcommando.boot;

import me.koply.kcommando.KCommando;
import me.koply.kcommando.internal.Kogger;
import me.koply.kcommando.internal.util.PackageReader;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassLooter {

    public static Set<Class<?>> getClasses(List<String> paths) {
        Set<Class<?>> classes = new HashSet<>();
        for (String path : paths) {
            Set<Class<?>> clazzez = getClassesFromPackage(path);
            if (clazzez != null) classes.addAll(clazzez);
        }
        return classes;
    }

    private static Set<Class<?>> getClassesFromPackage(String path) {
        try {
            Set<Class<?>> set = PackageReader.getAllClassesFromPackage(path);
            if (KCommando.verbose) {
                Kogger.info(set.size() + " class found from '" + path + "' package.");
            }
            return set;
        } catch (IOException ex) {
            Kogger.warn("An error occured while reading classes. Stacktrace: ");
            ex.printStackTrace();
        }

        return null;
    }

}