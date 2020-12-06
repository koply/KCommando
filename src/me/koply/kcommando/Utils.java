package me.koply.kcommando;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.Random;

public final class Utils {
    private static final Random random = new Random();
    public static MessageEmbed embed(Object o) {
        return new EmbedBuilder()
                .setDescription(o.toString())
                .setColor(randomColor())
                .build();
    }

    public static EmbedBuilder basicEmbed(Object o) {
        return new EmbedBuilder()
                .setDescription(o.toString())
                .setColor(randomColor());
    }

    public static Color randomColor() {
        return new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }
}