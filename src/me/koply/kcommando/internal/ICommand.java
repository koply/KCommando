package me.koply.kcommando.internal;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public interface ICommand {

    // you should override just one
    default boolean handle(@NotNull MessageReceivedEvent e) { return true; } // 0x01
    default boolean handle(@NotNull MessageReceivedEvent e, @NotNull String[] args) { return true; }// 0x02

}