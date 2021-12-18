package me.koply.kcommando.internal;

public abstract class Command<E> implements ICommand<E> {

    private final CommandInfo<E> info;
    public Command() {
        info = CargoTruck.getCargo();
    }
    public final CommandInfo<E> getInfo() { return info; }

}