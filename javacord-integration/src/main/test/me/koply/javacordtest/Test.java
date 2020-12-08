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

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String token = sc.nextLine();
        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        KCommando kcm = new KCommando(new Test(api))
                .setPackage(Test.class.getPackage().getName())
                .setPrefix(".")
                .build();
    }
}