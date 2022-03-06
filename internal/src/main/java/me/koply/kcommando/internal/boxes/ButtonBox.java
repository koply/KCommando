package me.koply.kcommando.internal.boxes;

import java.lang.reflect.Method;

public class ButtonBox extends Box {

    public final String name;

    public ButtonBox(Object instance, Method method, Class<?> clazz, String name) {
        super(instance, method, clazz);
        this.name = name;
    }

}