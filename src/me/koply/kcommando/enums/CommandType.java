package me.koply.kcommando.enums;

public enum CommandType {
    EVENT((byte) 0x01),ARGNEVENT((byte) 0x02);
    byte value;
    CommandType(byte value) {
        this.value = value;
    }
}