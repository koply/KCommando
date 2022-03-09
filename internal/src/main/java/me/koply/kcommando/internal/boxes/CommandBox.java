package me.koply.kcommando.internal.boxes;

import me.koply.kcommando.internal.annotations.HandleCommand;

import java.lang.reflect.Method;

public class CommandBox extends Box {

    public final CommandType commandType;
    public final ReturnType returnType;
    public final HandleCommand annotation;

    public CommandBox(Object instance, Method method, Class<?> clazz, CommandType commandType, ReturnType returnType, HandleCommand annotation) {
        super(instance, method, clazz);
        this.commandType = commandType;
        this.returnType = returnType;
        this.annotation = annotation;
    }

    public enum CommandType {
        EVENT, EVENT_ARGS, EVENT_ARGS_PREFIX;

        public static CommandType fromBoxType(int value) {
            switch (value > 3 ? value - 3 : value) {
                case 1:
                    return EVENT;
                case 2:
                    return EVENT_ARGS;
                case 3:
                    return EVENT_ARGS_PREFIX;
                default:
                    return null;
            }
        }
    }

    public enum ReturnType {
        BOOLEAN, VOID
    }

}