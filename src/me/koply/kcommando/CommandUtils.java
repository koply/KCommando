package me.koply.kcommando;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Random;

public abstract class CommandUtils {
    // you should override just one
    public void handle(@NotNull MessageReceivedEvent e) {} // 0x1
    public void handle(@NotNull MessageReceivedEvent e, @NotNull Params p) {} // 0x2
    public void handle(@NotNull MessageReceivedEvent e, @NotNull String[] args) {} // 0x3

    // utility godes
    Random random = new Random();
    public MessageEmbed embed(Object o) {
        return new EmbedBuilder()
                .setDescription(o.toString())
                .setColor(randomColor())
                .build();
    }

    public EmbedBuilder basicEmbed(Object o) {
        return new EmbedBuilder()
                .setDescription(o.toString())
                .setColor(randomColor());
    }

    public Color randomColor() {
        return new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    enum TYPE {
        EVENT((byte) 1),PARAMETEREDEVENT((byte) 2),ARGNEVENT((byte) 3);
        byte value;
        TYPE(byte value) {
            this.value = value;
        }
    }
}