package me.koply.kcommando.boot;

import me.koply.kcommando.internal.boxes.BoxType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnnotationBox {

    public final BoxType type;
    public final Annotation annotation;
    public final Method method;
    public final Class<?> clazz;
    public AnnotationBox(BoxType type, Annotation annotation, Method method, Class<?> clazz) {
        this.type = type;
        this.annotation = annotation;
        this.method = method;
        this.clazz = clazz;
    }

}