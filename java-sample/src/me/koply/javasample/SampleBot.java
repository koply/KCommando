package me.koply.javasample;

import me.koply.javasample.commands.SimpleCommands;
import me.koply.kcommando.CommandToRun;
import me.koply.kcommando.KCommando;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SampleBot {

    private final String token;
    public SampleBot(String token) {
        this.token = token;
    }

    public static final String PREFIX = ".";
    public static final EmbedBuilder COMMANDSEMBED = new EmbedBuilder();

    public void run() throws LoginException, InterruptedException {
        JDA jda = JDABuilder.createDefault(token).setAutoReconnect(true).build();
        jda.awaitReady();

        KCommando kcommando = new KCommando(jda)
                .setPrefix(PREFIX)
                .setPackage(SimpleCommands.class.getPackage().getName())
                .setOwners("269140308208517130")
                .setCooldown(5000L).build();

        initHelpEmbeds(kcommando.getParams().getCommandMethods());
    }

    private void initHelpEmbeds(HashMap<String, CommandToRun> map) {
        final HashSet<CommandToRun> set = new HashSet<>();
        final StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, CommandToRun> entry : map.entrySet()) {
            if (set.contains(entry.getValue())) continue;
            sb.append("`").append(entry.getValue().getClazz().getInfo().getName())
                    .append("` -> ").append(entry.getValue().getClazz().getInfo().getDescription()).append("\n");
            set.add(entry.getValue());
        }
        COMMANDSEMBED.addField("❯ Komutlar", sb.toString(), false)
                .addField("❯ Linkler", "[KCommando](https://github.com/MusaBrt/KCommando)", false);

    }
}