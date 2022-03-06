package me.koply.kcommando.internal.boxes;

public enum BoxType {
    
    // e -> event
    // ea -> event - args
    // eap -> event - args - prefix
    // _B -> boolean
    // _U -> usedCommand
    COMMAND_E(1),
    COMMAND_EA(2),
    COMMAND_EAP(3),
    COMMAND_E_B(4),
    COMMAND_EA_B(5),
    COMMAND_EAP_B(6),
    SLASH(7),
    BUTTON(8),
    SIMILAR_LIST(9),
    SIMILAR_SET(10),
    SIMILAR_LIST_U(11),
    SIMILAR_SET_U(12),
    UNKNOWN(-1);

    public final int value;
    BoxType(int value) {
        this.value = value;
    }

    public static BoxType fromValue(int value) {
        for (BoxType t : values()) {
            if (t.value == value) return t;
        }
        return UNKNOWN;
    }
}