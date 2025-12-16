package me.koply.kcommando.boot;

import me.koply.kcommando.KCommando;
import me.koply.kcommando.integration.Integration;
import me.koply.kcommando.internal.Kogger;
import me.koply.kcommando.internal.annotations.HandleButton;
import me.koply.kcommando.internal.annotations.HandleCommand;
import me.koply.kcommando.internal.annotations.HandleFalse;
import me.koply.kcommando.internal.annotations.HandleSlash;
import me.koply.kcommando.internal.annotations.SimilarCallback;
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

    public AnnotationChecks(final Integration integration) {
        this.integration = integration;
        this.list.put(HandleCommand.class, this::commandoCheck);
        this.list.put(HandleSlash.class, method1 -> this.slashCheck(method1) ? BoxType.SLASH : null);
        this.list.put(HandleButton.class, method1 -> this.buttonCheck(method1) ? BoxType.BUTTON : null);
        this.list.put(SimilarCallback.class, this::similarCallbackCheck);
        this.list.put(HandleFalse.class, method1 -> {
            final int val = this.commandoCheck(method1).value; // checks are equal so we could use this
            final int increment = val > 3 ? 9 : 12; // command type to handlefalsetype
            return BoxType.fromValue(val + increment);
        });
    }

    public AnnotationBox check(final Method method, final Class<? extends Annotation> annotationType) {

        final Annotation annotation = method.getAnnotation(annotationType);
        final Function<Method, BoxType> checkFunc = this.list.get(annotationType);
        final BoxType type = checkFunc.apply(method);

        // commando - slash - button
        return type == null ? null : new AnnotationBox(type, annotation, method, method.getDeclaringClass());
    }

    /**
     * internal
     * check list: return type, parameters (event, args, prefix)
     * @param method to be checked
     * @return if correct e-ea-eap[b] else unknown
     */
    private BoxType commandoCheck(final Method method) {
        final Class<?> returnType = method.getReturnType();
        final boolean isBoolean = returnType == Boolean.TYPE;
        final boolean isSuitable = isBoolean || returnType == Void.TYPE;
        if (!isSuitable) {
            if (KCommando.verbose) {
                Kogger.info("The return type of " + method.getName() + " neither Boolean nor Void.");
            }
            return BoxType.UNKNOWN;
        }

        BoxType type = BoxType.UNKNOWN;

        final Class<?>[] parameters = method.getParameterTypes();
        if (parameters.length <= 3) {
            final boolean event = parameters[0].equals(this.integration.getMessageEventType());
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
    private boolean slashCheck(final Method method) {
        final Class<?>[] parameters = method.getParameterTypes();
        return parameters[0].equals(this.integration.getSlashEventType());
    }

    /**
     * internal
     * @param method to be checked
     * @return returns true if method had correct parameter
     */
    private boolean buttonCheck(final Method method) {
        final Class<?>[] parameters = method.getParameterTypes();
        return parameters[0].equals(this.integration.getButtonEventType());
    }

    private BoxType similarCallbackCheck(final Method method) {
        // event - Set<String> similars - String usedCommand
        final Parameter[] params = method.getParameters();
        if (params.length < 2)
            return null;

        if (!params[0].getType().equals(this.integration.getMessageEventType()))
            return null;

        final String typename = params[1].getParameterizedType().getTypeName();

        final boolean islist = typename.equals("java.util.List<java.lang.String>");
        final boolean isset = typename.equals("java.util.Set<java.lang.String>");

        if (!(islist || isset))
            return null;

        int value = islist ? 9 : 10;

        if (params.length == 3 && params[2].getType() == String.class)
            value += 2;

        return BoxType.fromValue(value);
    }

}