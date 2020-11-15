package me.koply.kotlinsample.commands

import me.koply.kcommando.CommandUtils
import me.koply.kcommando.annotations.Command
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

@Command(names = ["ping", "pingu"], description = "Pong!")
class SimpleCommands : CommandUtils() {

    override fun handle(e: MessageReceivedEvent) {
        e.textChannel.sendMessage(embed("Pong!")).queue()
    }

}