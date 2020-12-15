package me.koply.javacordtest;

import me.koply.kcommando.KCommando;
import me.koply.kcommando.integration.impl.javacord.JavacordIntegration;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.util.Scanner;

public class Test extends JavacordIntegration {

    public Test(DiscordApi discordApi) {
        super(discordApi);
    }

    private static Test instance;
    public static Test getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String token = sc.nextLine();
        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        instance = new Test(api);

        KCommando kcm = new KCommando(instance)
                .setPackage(Test.class.getPackage().getName())
                .setPrefix(".")
                .build();
    }
}