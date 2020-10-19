package me.koply.kcommando;

import me.koply.kcommando.annotations.Command;
import java.lang.reflect.Method;

class CommandToRun {
    private Command commandAnnotation;
    private Method method;
    private Class<?> klass;
    private String groupName;

    protected Command getCommandAnnotation() {
        return commandAnnotation;
    }

    protected CommandToRun setCommandAnnotation(Command commandAnnotation) {
        this.commandAnnotation = commandAnnotation;
        return this;
    }

    protected Method getMethod() {
        return method;
    }

    protected CommandToRun setMethod(Method method) {
        this.method = method;
        return this;
    }

    protected Class<?> getKlass() {
        return klass;
    }

    protected CommandToRun setKlass(Class<?> klass) {
        this.klass = klass;
        return this;
    }

    protected String getGroupName() {
        return groupName;
    }

    protected CommandToRun setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }
}