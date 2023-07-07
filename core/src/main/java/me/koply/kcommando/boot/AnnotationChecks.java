package me.koply.kcommando.boot;

import me.koply.kcommando.KCommando;
import me.koply.kcommando.integration.Integration;
import me.koply.kcommando.internal.Kogger;
import me.koply.kcommando.internal.annotations.*;
import me.koply.kcommando.internal.boxes.BoxType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class AnnotationChecks {

    private final Integration integration;
    public final Map<Class<? extends Annotation>, Function<Method, BoxType>> list = new HashMap<>();

    public AnnotationChecks(Integration integration) {
        this.integration = integration;
        list.put(HandleCommand.class, this::commandoCheck);
        list.put(HandleSlash.class, method1 -> slashCheck(method1) ? BoxType.SLASH : null);
        list.put(HandleButton.class, method1 -> buttonCheck(method1) ? BoxType.BUTTON : null);
        list.put(SimilarCallback.class, this::similarCallbackCheck);
        list.put(HandleFalse.class, method1 -> {
            int val = commandoCheck(method1).value; // checks are equal so we could use this
            int increment = val > 3 ? 9 : 12; // command type to handlefalsetype
            return BoxType.fromValue(val + increment);
        });
    }

    public AnnotationBox check(Method method, Class<? extends Annotation> annotationType) {

        Annotation annotation = method.getAnnotation(annotationType);
        Function<Method, BoxType> checkFunc = list.get(annotationType);
        BoxType type = checkFunc.apply(method);

        // commando - slash - button
        return type == null ? null : new AnnotationBox(type, annotation, method, method.getDeclaringClass());
    }

    /**
     * internal
     * check list: return type, parameters (event, args, prefix)
     * @param method to be checked
     * @return if correct e-ea-eap[b] else unknown
     */
    private BoxType commandoCheck(Method method) {
        Class<?> returnType = method.getReturnType();
        boolean isBoolean = returnType.equals(Boolean.TYPE);
        boolean isOk = isBoolean || returnType == Void.TYPE;
        if (!isOk) {
            if (KCommando.verbose) {
                Kogger.info("The return type of " + method.getName() + " neither Boolean nor Void.");
            }
            return BoxType.UNKNOWN;
        }

        BoxType type = BoxType.UNKNOWN;

        Class<?>[] parameters = method.getParameterTypes();
        if (parameters.length <= 3) {
            boolean event = parameters[0].equals(integration.getMessageEventType());
            if (!event) {
                return BoxType.UNKNOWN;
            } else if (parameters.length == 1) {
                type = BoxType.COMMAND_E;
            } else if (parameters.length == 2 && parameters[1].isArray()) {// args[] ??
                type = BoxType.COMMAND_EA;
            } else if (parameters.length == 3 && parameters[1].isArray() && parameters[2] == String.class) {
                type = BoxType.COMMAND_EAP;
            }
        }

        return isBoolean && type != BoxType.UNKNOWN ? BoxType.fromValue(type.value+3) : type;
    }

    /**
     * internal
     * @param method to be checked
     * @return returns true if method had correct parameter
     */
    private boolean slashCheck(Method method) {
        Class<?>[] parameters = method.getParameterTypes();
        return parameters[0].equals(integration.getSlashEventType());
    }

    /**
     * internal
     * @param method to be checked
     * @return returns true if method had correct parameter
     */
    private boolean buttonCheck(Method method) {
        Class<?>[] parameters = method.getParameterTypes();
        return parameters[0].equals(integration.getButtonEventType());
    }

    private BoxType similarCallbackCheck(Method method) {
        // event - Set<String> similars - String usedCommand
        Parameter[] params = method.getParameters();
        if (params.length < 2)
            return null;

        if (!params[0].getType().equals(integration.getMessageEventType()))
            return null;

        String typename = params[1].getParameterizedType().getTypeName();

        boolean isList = typename.equals("java.util.List<java.lang.String>");
        boolean isSet = typename.equals("java.util.Set<java.lang.String>");

        if (!(isList || isSet))
            return null;

        int value = isList ? 9 : 10;

        if (params.length == 3 && params[2].getType() == String.class)
            value += 2;

        return BoxType.fromValue(value);
    }

}