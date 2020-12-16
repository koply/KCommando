package me.koply.jdatest;

import me.koply.kcommando.CommandInfo;
import me.koply.kcommando.KCommando;
import me.koply.kcommando.integration.impl.jda.JDAIntegration;
import me.koply.kcommando.internal.SuggestionsCallback;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
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

        instance.setSuggestionsCallback((SuggestionsCallback<MessageReceivedEvent>) (e,list) -> {
            if (list.isEmpty()) {
                e.getChannel().sendMessage("Command not found.").queue();
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (CommandInfo info : list) {
                sb.append(Arrays.toString(info.getAliases())).append(" - ");
            }
            e.getChannel().sendMessage("Last command is not found. Suggestions: \n"+sb.toString()).queue();
        });

        KCommando kcm = new KCommando(instance)
                .setPackage(Test.class.getPackage().getName())
                .setPrefix(".")
                .build();

    }
}