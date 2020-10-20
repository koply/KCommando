package me.koply.kotlinsample.commands

import me.koply.kcommando.CommandUtils
import me.koply.kcommando.annotations.Command
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class SimpleCommands : CommandUtils {

    @Command(names = ["ping", "pingu"], description = "Pong!")
    fun ping(e : MessageReceivedEvent) {
        e.textChannel.sendMessage(embed("Pong!")).queue()
    }

}