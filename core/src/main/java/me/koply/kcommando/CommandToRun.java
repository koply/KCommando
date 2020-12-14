package me.koply.kcommando;

import me.koply.kcommando.internal.CommandType;

public final class CommandToRun {

    public CommandToRun(Command clazz, String groupName, CommandType type) {
        this.clazz = clazz;
        this.groupName = groupName;
        this.type = type;
    }

    private final Command clazz;
    private final String groupName;
    private final CommandType type;

    public final Command getClazz() {
        return clazz;
    }

    public final String getGroupName() {
        return groupName;
    }

    public final CommandType getType() {
        return type;
    }
}