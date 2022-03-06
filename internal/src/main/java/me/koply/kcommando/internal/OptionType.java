package me.koply.kcommando.internal;

public enum OptionType {
    SUB_COMMAND(1),
    SUB_COMMAND_GROUP(2),
    STRING(3),
    INTEGER(4),
    BOOLEAN(5),
    USER(6),
    CHANNEL(7),
    ROLE(8),
    MENTIONABLE(9),
    UNKNOWN(-1);

    public final int value;

    OptionType(int value) {
        this.value = value;
    }

    /**
     * @param value the int value of the enum
     * @return the slashoption had given value. nullable*
     */
    public OptionType fromValue(int value) {
        OptionType[] values = OptionType.values();
        for (OptionType option : values) {
            if (option.value == value) return option;
        }
        return null;
    }
}