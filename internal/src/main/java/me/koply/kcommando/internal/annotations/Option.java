package me.koply.kcommando.internal.annotations;

import me.koply.kcommando.internal.OptionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Option {

    OptionType type();
    String name();
    String desc() default "-";
    boolean required() default false;

}