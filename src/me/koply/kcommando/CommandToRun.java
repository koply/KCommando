package me.koply.kcommando;

import me.koply.kcommando.annotations.Command;

public class CommandToRun {
    private Command commandAnnotation;
    private CommandUtils clazz;
    private String groupName;
    private boolean doubled;

    public Command getCommandAnnotation() {
        return commandAnnotation;
    }

    protected CommandToRun setCommandAnnotation(Command commandAnnotation) {
        this.commandAnnotation = commandAnnotation;
        return this;
    }

    protected CommandToRun setClazz(CommandUtils c) {
        clazz = c;
        return this;
    }
    protected CommandUtils getClazz() {
        return clazz;
    }


    public String getGroupName() {
        return groupName;
    }

    protected CommandToRun setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    protected CommandToRun setDoubled(boolean d) {
        doubled = d;
        return this;
    }

    public boolean isDoubled() {
        return doubled;
    }
}