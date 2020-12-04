package me.koply.kcommando;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Random;

public interface CommandUtils {
    // you should override just one
    default void handle(@NotNull MessageReceivedEvent e) {} // 0x01
    default void handle(@NotNull MessageReceivedEvent e, @NotNull Params p) {} // 0x02
    default void handle(@NotNull MessageReceivedEvent e, @NotNull String[] args) {} // 0x03

    // utility godes
    Random random = new Random();
    default MessageEmbed embed(Object o) {
        return new EmbedBuilder()
                .setDescription(o.toString())
                .setColor(randomColor())
                .build();
    }

    default EmbedBuilder basicEmbed(Object o) {
        return new EmbedBuilder()
                .setDescription(o.toString())
                .setColor(randomColor());
    }

    default Color randomColor() {
        return new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    enum TYPE {
        EVENT((byte) 0x01),PARAMETEREDEVENT((byte) 0x02),ARGNEVENT((byte) 0x03);
        byte value;
        TYPE(byte value) {
            this.value = value;
        }
    }
}