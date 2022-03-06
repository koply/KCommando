package me.koply.kcommando.internal.boxes;

import me.koply.kcommando.internal.annotations.HandleSlash;

import java.lang.reflect.Method;

public class SlashBox extends Box {

    public final HandleSlash info;

    public SlashBox(Object instance, Method method, Class<?> clazz, HandleSlash info) {
        super(instance, method, clazz);
        this.info = info;
    }

}