package me.koply.javacordtest;

import me.koply.kcommando.CommandInfo;
import me.koply.kcommando.CommandToRun;
import me.koply.kcommando.KCommando;
import me.koply.kcommando.Parameters;
import me.koply.kcommando.integration.impl.javacord.JavacordIntegration;
import me.koply.kcommando.internal.SuggestionsCallback;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

public class Test extends JavacordIntegration {

    public Test(DiscordApi discordApi) {
        super(discordApi);
    }

    private static Test instance;
    public static Test getInstance() {
        return instance;
    }

    public static void main(String[] args) throws IOException {
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

        File dataFile = new File("data.json");
        if (!dataFile.exists()) dataFile.createNewFile();

        KCommando kcm = new KCommando(instance)
                .setPackage(Test.class.getPackage().getName())
                .setPrefix(".")
                .setDataFile(dataFile)
                .build();
        instance.initHelpEmbed(kcm.getParameters());

    }

    private final EmbedBuilder helpEmbed = new EmbedBuilder();
    public EmbedBuilder getHelpEmbed() { return helpEmbed; } // clone not found lol
    private void initHelpEmbed(Parameters params) {
        // read only
        HashSet<String> duplicateChecker = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, CommandToRun> entry : params.getCommandMethods().entrySet()) {
            if (duplicateChecker.contains(entry.getKey())) continue;
            CommandInfo info = entry.getValue().getClazz().getInfo();
            sb.append("`").append(Arrays.toString(info.getAliases())).append("` -> ").append(info.getDescription()).append("\n");
            duplicateChecker.add(entry.getKey());
        }
        helpEmbed.setDescription(sb.toString());
    }
}