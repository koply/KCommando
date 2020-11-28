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

    public CommandToRun setCommandAnnotation(Command commandAnnotation) {
        this.commandAnnotation = commandAnnotation;
        return this;
    }

    public CommandToRun setClazz(CommandUtils c) {
        clazz = c;
        return this;
    }
    public CommandUtils getClazz() {
        return clazz;
    }


    public String getGroupName() {
        return groupName;
    }

    public CommandToRun setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public CommandToRun setDoubled(boolean d) {
        doubled = d;
        return this;
    }

    public boolean isDoubled() {
        return doubled;
    }
}