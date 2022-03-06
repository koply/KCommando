package me.koply.kcommando.internal.boxes;

import java.lang.reflect.Method;

public class SimilarBox extends Box {

    public final SimilarListType listType;
    public final boolean usedCommand;

    public SimilarBox(Object instance, Method method, Class<?> clazz, SimilarListType listType, boolean usedCommand) {
        super(instance, method, clazz);
        this.listType = listType;
        this.usedCommand = usedCommand;
    }

    public enum SimilarListType {
        SET, LIST
    }
}