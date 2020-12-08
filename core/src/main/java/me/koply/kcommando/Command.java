package me.koply.kcommando;

import me.koply.kcommando.internal.ICommand;

public abstract class Command<E> implements ICommand<E> {

    private final CommandInfo info;
    public Command() {
        info = KCommando.CargoTruck.getCargo();
    }
    public final CommandInfo getInfo() { return info; }

}