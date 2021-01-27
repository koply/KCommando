package me.koply.kcommando.internal;

public enum CommandType {
    EVENT((byte) 0x01), ARGNEVENT((byte) 0x02), PREFIXED((byte) 0x03);
    public final byte value;
    CommandType(byte value) {
        this.value = value;
    }
}