package me.koply.javacordtest;

import me.koply.kcommando.CommandInfo;
import me.koply.kcommando.KCommando;
import me.koply.kcommando.integration.impl.javacord.JavacordIntegration;
import me.koply.kcommando.internal.SuggestionsCallback;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.Arrays;
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

        instance.setSuggestionsCallback((SuggestionsCallback<MessageCreateEvent>) (e, list) -> {
            if (list.isEmpty()) {
                e.getChannel().sendMessage("Command not found.");
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (CommandInfo info : list) {
                sb.append(Arrays.toString(info.getAliases())).append(" - ");
            }
            e.getChannel().sendMessage("Last command is not found. Suggestions: \n"+sb.toString());
        });

        KCommando kcm = new KCommando(instance)
                .setPackage(Test.class.getPackage().getName())
                .setPrefix(".")
                .build();
    }
}