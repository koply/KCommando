package me.koply.kcommando.internal.boxes;

import me.koply.kcommando.internal.annotations.HandleSlash;
import me.koply.kcommando.internal.annotations.Perm;

import java.lang.reflect.Method;
import java.util.Optional;

public class SlashBox extends Box {

    public final HandleSlash info;

    public SlashBox(Object instance, Method method, Class<?> clazz, HandleSlash info) {
        super(instance, method, clazz);
        this.info = info;
    }

    public Optional<Perm> getPerm() {
        Perm annotation = method.getAnnotation(Perm.class);
        return annotation == null ? Optional.empty() : Optional.of(annotation);
    }

}