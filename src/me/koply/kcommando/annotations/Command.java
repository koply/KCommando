package me.koply.kcommando.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {
    String[] names();
    String description();
    boolean privateOnly() default false;
    boolean guildOnly() default false;
    boolean ownerOnly() default false;
    boolean sync() default false;
}