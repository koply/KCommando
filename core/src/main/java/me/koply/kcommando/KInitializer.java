package me.koply.kcommando;

import me.koply.kcommando.internal.Kogger;
import me.koply.kcommando.internal.annotations.HandleButton;
import me.koply.kcommando.internal.annotations.HandleCommand;
import me.koply.kcommando.internal.annotations.SimilarCallback;
import me.koply.kcommando.internal.annotations.HandleSlash;
import me.koply.kcommando.internal.boxes.*;
import me.koply.kcommando.internal.util.PackageReader;
import me.koply.kcommando.manager.ButtonManager;
import me.koply.kcommando.manager.CommandManager;
import me.koply.kcommando.manager.SlashManager;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KInitializer {

    private final KCommando main;
    private final SlashManager slashManager;
    private final CommandManager commandManager;
    private final ButtonManager buttonManager;
    public KInitializer(KCommando main) {
        this.main = main;
        slashManager = new SlashManager();
        commandManager = new CommandManager(main);
        buttonManager = new ButtonManager();
    }

    private static class AnnotationBox {
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

    /**
     * Registers everything.
     */
    public void build() {
        Kogger.info("Build start...");
        if (main.getPackagePaths().isEmpty()) {
            throw new IllegalArgumentException("Please add package path for search.");
        }

        Set<Class<?>> classes = getClasses();
        List<AnnotationBox> methods = getSuitableMethods(classes);

        // registers all boxes to handlers
        methods.forEach(this::registerBox);

        buttonManager.registerManager(main.integration);
        commandManager.registerManager(main.integration);
        slashManager.registerManager(main.integration);
        Kogger.info("Build done!");
    }

    /**
     * @param instance an instance of a class that includes (command-slash-button)
     */
    public void registerClass(Object instance) {
        Class<?> clazz = instance.getClass();
        List<AnnotationBox> methods = analyzeClass(clazz);
        if (methods == null || methods.isEmpty()) return;
        methods.forEach(box -> registerBoxWithInstance(instance, box));
    }

    // private api --------------------------------------

    // internal
    private void registerSlashBox(Object instance, AnnotationBox box) {
        HandleSlash ann = (HandleSlash) box.annotation;
        SlashBox slashBox = new SlashBox(instance, box.method, box.clazz, ann);

        main.integration.registerSlashCommand(ann);
        slashManager.commands.put(ann.name(), slashBox);
    }

    // internal
    private void registerCommandBox(Object instance, AnnotationBox box) {
        HandleCommand ann = (HandleCommand) box.annotation;

        int type = box.type.value;
        boolean isboolean = type > 3;
        CommandBox commandBox = new CommandBox(instance, box.method, box.clazz,
                CommandBox.CommandType.fromBoxType(type),
                isboolean ? CommandBox.ReturnType.BOOLEAN : CommandBox.ReturnType.VOID);

        for (String alias : ann.aliases()) {
            commandManager.commands.put(alias, commandBox);
        }
    }

    // internal
    private void registerButtonBox(Object instance, AnnotationBox box) {
        HandleButton ann = (HandleButton) box.annotation;

        ButtonBox buttonBox = new ButtonBox(instance, box.method, box.clazz, ann.value());
        buttonManager.buttons.put(ann.value(), buttonBox);
    }

    // internal
    private void registerSimilarBox(Object instance, AnnotationBox box) {
        // we can get annotation but we don't need that
        boolean usedCommand = box.type.value > 10;
        int value = usedCommand ? box.type.value-2 : box.type.value;
        SimilarBox.SimilarListType type = value == 9 ? SimilarBox.SimilarListType.LIST : SimilarBox.SimilarListType.SET;

        SimilarBox similarBox = new SimilarBox(instance, box.method, box.clazz, type, usedCommand);
        commandManager.setSimilarCallback(similarBox);
    }

    // private api
    private void registerBoxWithInstance(Object instance, AnnotationBox box) {
        if (box.type == BoxType.SLASH) {
            registerSlashBox(instance, box);
        } else if (box.type.value > 0 && box.type.value < 7) {
            registerCommandBox(instance, box);
        } else if (box.type == BoxType.BUTTON) {
            registerButtonBox(instance, box);
        } else if (box.type.value > 8) {
            registerSimilarBox(instance, box);
        }
    }

    // private api
    private void registerBox(AnnotationBox box) {
        try {
            Object instance = null;
            if (!Modifier.isStatic(box.method.getModifiers())) {
                //noinspection ConfusingArgumentToVarargsMethod
                Constructor<?> constructor = box.clazz.getDeclaredConstructor(null);
                instance = constructor.newInstance();
            }

            registerBoxWithInstance(instance, box);
        } catch (NoSuchMethodException ex) {
            if (KCommando.verbose) {
                Kogger.warn(box.clazz.getName() + " doesn't have any parameterless constructor. You can manually register your class with KCommando#registerClass(Object)");
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            if (KCommando.verbose) {
                Kogger.warn("An error occur while registering the class named as " + box.clazz.getName());
            }
        }
    }

    /**
     * internal
     * @param method the method to be checked
     * @return if method public returns true
     */
    private boolean methodPreCheck(Method method) {
        method.setAccessible(true);
        if (!Modifier.isPublic(method.getModifiers())) {
            if (KCommando.verbose) {
                Kogger.info(method.getName() + " is not public.");
            }
            return false;
        }
        return true;
    }

    /**
     * internal
     * check list: return type, parameters (event, args, prefix)
     * @param method to be checked
     * @return if correct e-ea-eap[b] else unknown
     */
    private BoxType commandoCheck(Method method) {
        Class<?> returnType = method.getReturnType();
        boolean isboolean;
        boolean isok = (isboolean = returnType == Boolean.TYPE) || returnType == Void.TYPE;
        if (!isok) return BoxType.UNKNOWN;

        BoxType type = BoxType.UNKNOWN;

        Class<?>[] parameters = method.getParameterTypes();
        if (parameters.length <= 3) {
            boolean event = parameters[0].equals(main.integration.getMessageEventType());
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

        return isboolean && type != BoxType.UNKNOWN ? BoxType.fromValue(type.value+3) : type;
    }

    /**
     * internal
     * @param method to be checked
     * @return returns true if method had correct parameter
     */
    private boolean slashCheck(Method method) {
        Class<?>[] parameters = method.getParameterTypes();
        return parameters[0].equals(main.integration.getSlashEventType());
    }

    /**
     * internal
     * @param method to be checked
     * @return returns true if method had correct parameter
     */
    private boolean buttonCheck(Method method) {
        Class<?>[] parameters = method.getParameterTypes();
        return parameters[0].equals(main.integration.getButtonEventType());
    }

    private BoxType similarCallbackCheck(Method method) {
        // event - Set<String> similars - String usedCommand
        Parameter[] params = method.getParameters();
        if (params.length < 2)
            return null;

        if (!params[0].getType().equals(main.integration.getMessageEventType()))
            return null;

        String typename = params[1].getParameterizedType().getTypeName();

        boolean islist = typename.equals("java.util.List<java.lang.String>");
        boolean isset = typename.equals("java.util.Set<java.lang.String>");

        if (!(islist || isset))
            return null;

        int value = islist ? 9 : 10;

        if (params.length == 3 && params[2].getType() == String.class)
            value += 2;

        return BoxType.fromValue(value);
    }

    // internal
    private AnnotationBox annotationChecks(Method method) {
        BoxType type = null;

        Annotation commando = method.getAnnotation(HandleCommand.class);
        Annotation slash = method.getAnnotation(HandleSlash.class);
        Annotation button = method.getAnnotation(HandleButton.class);
        Annotation sugg = method.getAnnotation(SimilarCallback.class);

        Annotation ret = null;
        if (commando != null) {
            type = commandoCheck(method);
            ret = commando;
        } else if (slash != null) {
            type = slashCheck(method) ? BoxType.SLASH : null;
            ret = slash;
        } else if (button != null) {
            type = buttonCheck(method) ? BoxType.BUTTON : null;
            ret = button;
        } else if (sugg != null) {
            type = similarCallbackCheck(method);
            ret = sugg;
        }

        // commando - slash - button
        return type == null ? null : new AnnotationBox(type, ret, method, method.getDeclaringClass());
    }

    // internal
    private List<AnnotationBox> getMethodsFromClazz(Class<?> clazz) {
        // matching methods
        List<AnnotationBox> ret = new ArrayList<>();

        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            // checks the modifier is public or not
            // and gets the AnnotationBox
            AnnotationBox box;
            if ((box = annotationChecks(method)) == null || !methodPreCheck(method)) {
                continue;
            }
            ret.add(box);
        }
        return ret;
    }

    // private api method
    private List<AnnotationBox> analyzeClass(Class<?> clazz) {
        // class public check
        if (!Modifier.isPublic(clazz.getModifiers())) {
            if (KCommando.verbose) {
                Kogger.info(clazz.getName() + " is not public.");
            }
            return null;
        }

        List<AnnotationBox> methods = getMethodsFromClazz(clazz);
        if (methods.isEmpty() && KCommando.verbose) {
            Kogger.info(clazz.getName() + " doesn't have any command/slash/button method.");
        }
        return methods;
    }

    // private api method
    private List<AnnotationBox> getSuitableMethods(Set<Class<?>> classes) {
        List<AnnotationBox> ret = new ArrayList<>();
        for (Class<?> clazz : classes) {
            List<AnnotationBox> v = analyzeClass(clazz);
            if (v != null && !v.isEmpty()) ret.addAll(v);
        }
        return ret;
    }

    // private api method
    private Set<Class<?>> getClasses() {
        List<String> paths = main.getPackagePaths();
        Set<Class<?>> classes = new HashSet<>();
        for (String path : paths) {
            Set<Class<?>> clazzez = getClassesFromPackage(path);
            if (clazzez != null) classes.addAll(clazzez);
        }
        return classes;
    }

    // internal
    private Set<Class<?>> getClassesFromPackage(String path) {
        try {
            Set<Class<?>> set = PackageReader.getAllClassesFromPackage(path);
            if (KCommando.verbose) {
                Kogger.info(set.size() + " class found from '" + path + "' package.");
            }
            return set;
        } catch (IOException ex) {
            Kogger.warn("An error occured while reading classes. Stacktrace: ");
            ex.printStackTrace();
        }

        return null;
    }

}