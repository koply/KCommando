package me.koply.kcommando;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Random;

public abstract class CommandUtils {
    // you should override just one
    public void handle(MessageReceivedEvent e) {}
    public void handle(MessageReceivedEvent e, Params p) {}

    // utility godes
    Random random = new Random();
    public MessageEmbed embed(String str) {
        return new EmbedBuilder()
                .setDescription(str)
                .setColor(randomColor())
                .build();
    }

    public EmbedBuilder basicEmbed(String str) {
        return new EmbedBuilder()
                .setDescription(str)
                .setColor(randomColor());
    }

    public Color randomColor() {
        return new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }
}