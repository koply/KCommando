package me.koply.kcommando.internal.boxes;

import java.lang.reflect.Method;

public class FalseBox extends Box {

    public final BoxType type;

    // method includes methodName
    public FalseBox(Object instance, Method method, Class<?> clazz, BoxType type) {
        super(instance, method, clazz);
        this.type = type;
    }
}