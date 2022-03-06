package me.koply.kcommando.internal.boxes;

import java.lang.reflect.Method;

public abstract class Box {

    public final Object instance;
    public final Method method;
    public final Class<?> clazz;

    public Box(Object instance, Method method, Class<?> clazz) {
        this.instance = instance;
        this.method = method;
        this.clazz = clazz;
    }
}