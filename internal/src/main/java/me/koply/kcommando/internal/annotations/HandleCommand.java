package me.koply.kcommando.internal.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HandleCommand {
    String name();
    String[] aliases();
    String description() default "-";
    String falseMethod() default "-";
    boolean guildOnly() default false;
    boolean ownerOnly() default false;
    boolean privateOnly() default false;
}