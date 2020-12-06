package me.koply.kotlinsample.commands

import me.koply.kcommando.Command
import me.koply.kcommando.Utils
import me.koply.kcommando.internal.Commander
import me.koply.kotlinsample.SampleBot
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

@Commander(name = "Help",
        aliases = ["help","yardım"],
        description="Bu komut!")
class HelpCommand : Command() {

    override fun handle(e: MessageReceivedEvent) : Boolean {
        e.textChannel.sendMessage(SampleBot.commandsEmbed
                .setAuthor(e.author.name, null, e.author.avatarUrl)
                .setFooter(e.jda.selfUser.name, e.jda.selfUser.avatarUrl)
                .setColor(Utils.randomColor())
                .build()).queue()
        return true;
    }
}