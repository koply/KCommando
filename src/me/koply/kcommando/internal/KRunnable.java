package me.koply.kcommando.internal;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface KRunnable {
    void run(MessageReceivedEvent e);
}