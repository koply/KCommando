package me.koply.kcommando;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.Random;

public interface CommandUtils {
    Random random = new Random();
    default MessageEmbed embed(String str) {
        return new EmbedBuilder()
                .setDescription(str)
                .setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)))
                .build();
    }
}