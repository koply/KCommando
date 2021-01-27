package me.koply.kcommando.internal;

public class CargoTruck {

    private static CommandInfo cargo;
    public static void setCargo(CommandInfo cargo1) {
        cargo = cargo1;
    }
    public static CommandInfo getCargo() {
        return cargo;
    }

}