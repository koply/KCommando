package me.koply.kcommando.internal.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Commando {
    String name();
    String[] aliases();
    String description() default "-";
    boolean privateOnly() default false;
    boolean guildOnly() default false;
    boolean ownerOnly() default false;
    boolean sync() default false;
    boolean onlyArguments() default false;
}