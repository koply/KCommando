package me.koply.javasample;

import me.koply.javasample.commands.SimpleCommands;
import me.koply.kcommando.KCommando;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class SampleBot {

    private final String token;

    public SampleBot(String token) {
        this.token = token;
    }

    public void run() throws LoginException, InterruptedException {
        JDA jda = JDABuilder.createDefault(token).setAutoReconnect(true).build();
        jda.awaitReady();

        KCommando kcommando = new KCommando(jda)
                .setPrefix(".")
                .setPackage(SimpleCommands.class.getPackage().getName())
                .setOwners("269140308208517130")
                .setCooldown(5000L).build();
    }

}