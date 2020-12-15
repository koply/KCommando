package me.koply.jdatest;

import me.koply.kcommando.KCommando;
import me.koply.kcommando.integration.impl.jda.JDAIntegration;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import java.util.Scanner;

public class Test extends JDAIntegration {

    public Test(JDA jda) {
        super(jda);
    }

    private static Test instance;
    public static Test getInstance() { return instance; }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        String token = sc.nextLine();
        JDA jda = JDABuilder.createLight(token).build();
        jda.awaitReady();

        instance = new Test(jda);

        KCommando kcm = new KCommando(instance)
                .setPackage(Test.class.getPackage().getName())
                .setPrefix(".")
                .build();

    }
}