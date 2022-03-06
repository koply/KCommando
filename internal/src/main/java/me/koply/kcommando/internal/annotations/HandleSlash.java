package me.koply.kcommando.internal.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HandleSlash {

    String name();
    String desc() default "-";
    Option[] options();
    boolean global() default false;
    long[] guildId() default 0;

}