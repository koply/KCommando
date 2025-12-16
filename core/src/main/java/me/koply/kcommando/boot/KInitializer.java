package me.koply.kcommando.boot;

import me.koply.kcommando.KCommando;
import me.koply.kcommando.internal.Kogger;
import me.koply.kcommando.internal.annotations.HandleButton;
import me.koply.kcommando.internal.annotations.HandleCommand;
import me.koply.kcommando.internal.annotations.HandleSlash;
import me.koply.kcommando.internal.boxes.BoxType;
import me.koply.kcommando.internal.boxes.ButtonBox;
import me.koply.kcommando.internal.boxes.CommandBox;
import me.koply.kcommando.internal.boxes.FalseBox;
import me.koply.kcommando.internal.boxes.SimilarBox;
import me.koply.kcommando.internal.boxes.SlashBox;
import me.koply.kcommando.manager.ButtonManager;
import me.koply.kcommando.manager.CommandManager;
import me.koply.kcommando.manager.SlashManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class KInitializer {

    private final KCommando main;
    private final SlashManager slashManager;
    private final CommandManager commandManager;
    private final ButtonManager buttonManager;

    private final AnnotationChecks annotationChecks;

    public KInitializer(final KCommando main) {
        this.main = main;
        this.slashManager = new SlashManager();
        this.commandManager = new CommandManager(main);
        this.buttonManager = new ButtonManager();
        this.annotationChecks = new AnnotationChecks(main.integration);
    }

    /**
     * Registers everything.
     */
    public void build() {
        Kogger.info("Build start...");
        if (this.main.getPackages().isEmpty()) {
            throw new IllegalArgumentException("Please add package path for search.");
        }

        final Set<Class<?>> classes = ClassLooter.getClasses(this.main.getPackages());
        final List<AnnotationBox> methods = this.getSuitableMethods(classes);

        // registers all boxes to handlers
        methods.forEach(this::registerBox);

        this.buttonManager.registerManager(this.main.integration);
        this.commandManager.registerManager(this.main.integration);
        this.slashManager.registerManager(this.main.integration);
        Kogger.info("Build done!");
    }

    /**
     * @param instance an instance of a class that includes (command-slash-button)
     */
    public void registerClass(final Object instance) {
        final Class<?> clazz = instance.getClass();
        final List<AnnotationBox> methods = this.analyzeClass(clazz);
        if (methods == null || methods.isEmpty()) return;
        methods.forEach(box -> this.registerBoxWithInstance(instance, box));
    }

    // private api --------------------------------------

    // internal
    private void registerSlashBox(final Object instance, final AnnotationBox box) {
        final HandleSlash ann = (HandleSlash) box.annotation;
        final SlashBox slashBox = new SlashBox(instance, box.method, box.clazz, ann);

        this.main.integration.registerSlashCommand(slashBox);
        this.slashManager.commands.put(ann.name(), slashBox);
    }

    // internal
    private void registerCommandBox(final Object instance, final AnnotationBox box) {
        final HandleCommand ann = (HandleCommand) box.annotation;

        final int type = box.type.value;
        final boolean isboolean = type > 3;
        final CommandBox commandBox = new CommandBox(instance, box.method, box.clazz,
                CommandBox.CommandType.fromBoxType(type),
                isboolean ? CommandBox.ReturnType.BOOLEAN : CommandBox.ReturnType.VOID, ann);

        for (final String alias : ann.aliases()) {
            this.commandManager.commands.put(alias, commandBox);
        }
    }

    // internal
    private void registerButtonBox(final Object instance, final AnnotationBox box) {
        final HandleButton ann = (HandleButton) box.annotation;

        final ButtonBox buttonBox = new ButtonBox(instance, box.method, box.clazz, ann.value());
        this.buttonManager.buttons.put(ann.value(), buttonBox);
    }

    // internal
    private void registerSimilarBox(final Object instance, final AnnotationBox box) {
        // we can get annotation but we don't need that
        final boolean usedCommand = box.type.value > 10;
        final int value = usedCommand ? box.type.value-2 : box.type.value;
        final SimilarBox.SimilarListType type = value == 9 ? SimilarBox.SimilarListType.LIST : SimilarBox.SimilarListType.SET;

        final SimilarBox similarBox = new SimilarBox(instance, box.method, box.clazz, type, usedCommand);
        this.commandManager.setSimilarCallback(similarBox);
    }

    // internal
    private void registerHandleFalseBox(final Object instance, final AnnotationBox box) {
        final FalseBox falseBox = new FalseBox(instance, box.method, box.clazz, box.type);
        this.commandManager.falseBoxMap.put(box.method.getName(), falseBox);
    }

    // private api
    private void registerBoxWithInstance(final Object instance, final AnnotationBox box) {
        if (box.type == BoxType.SLASH) {
            this.registerSlashBox(instance, box);
        } else if (box.type.value > 0 && box.type.value < 7) {
            this.registerCommandBox(instance, box);
        } else if (box.type == BoxType.BUTTON) {
            this.registerButtonBox(instance, box);
        } else if (box.type.value > 8 && box.type.value < 13) {
            this.registerSimilarBox(instance, box);
        } else if (box.type.value >= 13) {
            this.registerHandleFalseBox(instance, box);
        }
    }

    // private api
    private void registerBox(final AnnotationBox box) {
        try {
            Object instance = null;
            if (!Modifier.isStatic(box.method.getModifiers())) {
                //noinspection ConfusingArgumentToVarargsMethod
                final Constructor<?> constructor = box.clazz.getDeclaredConstructor(null);
                instance = constructor.newInstance();
            }

            this.registerBoxWithInstance(instance, box);
        } catch (final NoSuchMethodException ex) {
            if (KCommando.verbose) {
                Kogger.warn(box.clazz.getName() + " doesn't have any parameterless constructor. You can manually register your class with KCommando#registerClass(Object)");
            }
        } catch (final InvocationTargetException | InstantiationException | IllegalAccessException e) {
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
    private boolean methodPreCheck(final Method method) {
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
    private List<AnnotationBox> getMethodsFromClazz(final Class<?> clazz) {
        // matching methods
        final List<AnnotationBox> ret = new ArrayList<>();

        final Collection<String> skippedMethods = new ArrayList<>();
        final Method[] methods = clazz.getMethods();
        for (final Method method : methods) {
            // we need at least 1 annotation to process method, if not we can continue
            final Annotation[] annotations = method.getDeclaredAnnotations();
            if (annotations.length == 0) continue;

            Class<? extends Annotation> annotationType = null;
            for (final Annotation ant : annotations) {
                if (this.annotationChecks.list.containsKey(ant.annotationType())) {
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
            final AnnotationBox box = this.annotationChecks.check(method, annotationType);
            if (box == null || !this.methodPreCheck(method)) {
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
    private List<AnnotationBox> analyzeClass(final Class<?> clazz) {
        // class public check
        if (!Modifier.isPublic(clazz.getModifiers())) {
            if (KCommando.verbose) {
                Kogger.info(clazz.getName() + " is not public.");
            }
            return null;
        }

        final List<AnnotationBox> methods = this.getMethodsFromClazz(clazz);
        if (methods.isEmpty() && KCommando.verbose) {
            Kogger.info(clazz.getName() + " doesn't have any command/slash/button method.");
        }
        return methods;
    }

    // private api method
    private List<AnnotationBox> getSuitableMethods(final Iterable<Class<?>> classes) {
        final List<AnnotationBox> ret = new ArrayList<>();
        for (final Class<?> clazz : classes) {
            final List<AnnotationBox> v = this.analyzeClass(clazz);
            if (v != null && !v.isEmpty()) ret.addAll(v);
        }
        return ret;
    }



}