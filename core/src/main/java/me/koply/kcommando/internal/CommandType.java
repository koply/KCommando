package me.koply.kcommando.internal;

public enum CommandType {
    EVENT((byte) 0x01), ARGNEVENT((byte) 0x02);
    public byte value;
    CommandType(byte value) {
        this.value = value;
    }
}