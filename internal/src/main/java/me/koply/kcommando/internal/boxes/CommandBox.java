package me.koply.kcommando.internal.boxes;

import java.lang.reflect.Method;

public class CommandBox extends Box {

    public final CommandType commandType;
    public final ReturnType returnType;

    public CommandBox(Object instance, Method method, Class<?> clazz, CommandType commandType, ReturnType returnType) {
        super(instance, method, clazz);
        this.commandType = commandType;
        this.returnType = returnType;
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