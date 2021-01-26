package me.koply.kcommando;

import me.koply.kcommando.internal.CargoTruck;
import me.koply.kcommando.internal.ICommand;

public abstract class Command<E> implements ICommand<E> {

    private final CommandInfo<E> info;
    public Command() {
        info = CargoTruck.getCargo();
    }
    public final CommandInfo<E> getInfo() { return info; }

}