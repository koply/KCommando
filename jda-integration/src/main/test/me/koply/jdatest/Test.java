package me.koply.jdatest;

import me.koply.kcommando.CommandToRun;
import me.koply.kcommando.KCommando;
import me.koply.kcommando.Parameters;
import me.koply.kcommando.integration.impl.jda.JDAIntegration;
import me.koply.kcommando.internal.CommandInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
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

        instance.setSuggestionsCallback((e, list) -> {
            if (list.isEmpty()) {
                e.getChannel().sendMessage("Command not found.").queue();
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (CommandInfo<MessageReceivedEvent> info : list) {
                sb.append(Arrays.toString(info.getAliases())).append(" - ");
            }
            e.getChannel().sendMessage("Last command is not found. Suggestions: \n"+sb.toString()).queue();
        });

        File dataFile = new File("data.json");
        if (!dataFile.exists()) dataFile.createNewFile();

        KCommando<MessageReceivedEvent> kcm = new KCommando<>(instance)
                .setPackage(Test.class.getPackage().getName())
                .setPrefix(".")
                .setDataFile(dataFile)
                .setPluginsPath(new File("jdaplugins/"))
                .build();

        instance.initHelpEmbed(kcm.getParameters());

    }

    private final EmbedBuilder helpEmbed = new EmbedBuilder();
    public EmbedBuilder getHelpEmbed() { return new EmbedBuilder(helpEmbed); }
    private void initHelpEmbed(Parameters<MessageReceivedEvent> params) {
        // read only
        HashSet<String> duplicateChecker = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, CommandToRun<MessageReceivedEvent>> entry : params.getCommandMethods().entrySet()) {
            if (duplicateChecker.contains(entry.getKey())) continue;
            CommandInfo<MessageReceivedEvent> info = entry.getValue().getClazz().getInfo();
               sb.append("`").append(Arrays.toString(info.getAliases())).append("` -> ").append(info.getDescription()).append("\n");
            duplicateChecker.add(entry.getKey());
        }
        helpEmbed.setDescription(sb.toString());
    }
}