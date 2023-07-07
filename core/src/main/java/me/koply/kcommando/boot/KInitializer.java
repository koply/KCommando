package me.koply.kcommando.boot;

import me.koply.kcommando.KCommando;
import me.koply.kcommando.internal.Kogger;
import me.koply.kcommando.internal.annotations.HandleButton;
import me.koply.kcommando.internal.annotations.HandleCommand;
import me.koply.kcommando.internal.annotations.HandleSlash;
import me.koply.kcommando.internal.boxes.*;
import me.koply.kcommando.manager.ButtonManager;
import me.koply.kcommando.manager.CommandManager;
import me.koply.kcommando.manager.SlashManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class KInitializer {

    private final KCommando main;
    private final SlashManager slashManager;
    private final CommandManager commandManager;
    private final ButtonManager buttonManager;

    private final AnnotationChecks annotationChecks;

    public KInitializer(KCommando main) {
        this.main = main;
        slashManager = new SlashManager();
        commandManager = new CommandManager(main);
        buttonManager = new ButtonManager();
        annotationChecks = new AnnotationChecks(main.integration);
    }

    /**
     * Registers everything.
     */
    public void build() {
        Kogger.info("Build start...");
        if (main.getPackages().isEmpty()) {
            throw new IllegalArgumentException("Please add package path for search.");
        }

        Set<Class<?>> classes = ClassLooter.getClasses(main.getPackages());
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

        main.integration.registerSlashCommand(slashBox);
        slashManager.commands.put(ann.name(), slashBox);
    }

    // internal
    private void registerCommandBox(Object instance, AnnotationBox box) {
        HandleCommand ann = (HandleCommand) box.annotation;

        int type = box.type.value;
        boolean isBoolean = type > 3;
        CommandBox commandBox = new CommandBox(instance, box.method, box.clazz,
                CommandBox.CommandType.fromBoxType(type),
                isBoolean ? CommandBox.ReturnType.BOOLEAN : CommandBox.ReturnType.VOID, ann);

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

    // internal
    private void registerHandleFalseBox(Object instance, AnnotationBox box) {
        FalseBox falseBox = new FalseBox(instance, box.method, box.clazz, box.type);
        commandManager.falseBoxMap.put(box.method.getName(), falseBox);
    }

    // private api
    private void registerBoxWithInstance(Object instance, AnnotationBox box) {
        if (box.type == BoxType.SLASH) {
            registerSlashBox(instance, box);
        } else if (box.type.value > 0 && box.type.value < 7) {
            registerCommandBox(instance, box);
        } else if (box.type == BoxType.BUTTON) {
            registerButtonBox(instance, box);
        } else if (box.type.value > 8 && box.type.value < 13) {
            registerSimilarBox(instance, box);
        } else if (box.type.value >= 13) {
            registerHandleFalseBox(instance, box);
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

    // internal
    private List<AnnotationBox> getMethodsFromClazz(Class<?> clazz) {
        // matching methods
        List<AnnotationBox> ret = new ArrayList<>();

        List<String> skippedMethods = new ArrayList<>();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            // we need at least 1 annotation to process method, if not we can continue
            Annotation[] annotations = method.getDeclaredAnnotations();
            if (annotations.length == 0) continue;

            Class<? extends Annotation> annotationType = null;
            for (Annotation ant : annotations) {
                if (annotationChecks.list.containsKey(ant.annotationType())) {
                    annotationType = ant.annotationType();
                }
            }

            if (annotationType == null) {
                if (KCommando.verbose) {
                    skippedMethods.add(method.getName());
                }
                continue;
            }

            // checks the modifier is public or not
            // and gets the AnnotationBox
            AnnotationBox box;
            if ((box = annotationChecks.check(method, annotationType)) == null || !methodPreCheck(method)) {
                continue;
            }
            ret.add(box);
        }
        if (KCommando.verbose) {
            Kogger.info("The skipped methods at " + clazz.getName() + ": "
                    + String.join(",", skippedMethods) + " (They don't have any appropriate annotation)");
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



}