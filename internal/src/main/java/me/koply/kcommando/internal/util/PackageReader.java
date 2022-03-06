package me.koply.kcommando.internal.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class PackageReader {

    // inspired from MaeveS2/SnowballNebula
    public static Set<Class<?>> getAllClassesFromPackage(String packagePath) throws IOException {
        InputStream systemStream = ClassLoader.getSystemClassLoader().getResourceAsStream(packagePath.replaceAll("[.]", "/"));
        InputStream use = systemStream == null ? PackageReader.class.getClassLoader().getResourceAsStream(packagePath.replaceAll("[.]", "/")) : systemStream;

        Set<Class<?>> classSet = new HashSet<>();
        if (use == null) return classSet;

        BufferedReader reader = new BufferedReader(new InputStreamReader(use));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.endsWith(".class")) {
                Class<?> clazz = getClassFromPackage(line, packagePath);
                if (clazz != null) classSet.add(clazz);
            }
        }

        return classSet;
    }

    private static Class<?> getClassFromPackage(String className, String packageName) {
        String clazz = packageName + "."
                + className.substring(0, className.lastIndexOf('.'));
        try {
            return Class.forName(clazz);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

}