package me.koply.kcommando.internal.annotations;

import me.koply.kcommando.internal.DefaultConstants;
import me.koply.kcommando.internal.OptionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HandleSlash {

    String name();

    String desc() default "-";

    Option[] options() default @Option(type = OptionType.UNKNOWN, name = DefaultConstants.DEFAULT_TEXT);

    boolean enabledInDms() default false;

    boolean global() default false;

    long[] guildId() default 0;

    boolean subCommand() default false;

    String parentGroup() default "";

}