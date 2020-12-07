package me.koply.kotlinsample.commands

import me.koply.kcommando.Command
import me.koply.kcommando.Utils
import me.koply.kcommando.internal.Commando
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

@Commando(name = "Ping",
        aliases = ["ping", "pingu"],
        description = "Pong!")
class SimpleCommands : Command() {

    override fun handle(e: MessageReceivedEvent) : Boolean {
        e.textChannel.sendMessage(Utils.embed("Pong!")).queue()
        return true
    }

}