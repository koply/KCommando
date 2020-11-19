package me.koply.kotlinsample.commands

import me.koply.kcommando.CommandUtils
import me.koply.kcommando.annotations.Command
import me.koply.kotlinsample.SampleBot
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

@Command(names = ["help","yardım"], description="Bu komut!")
class HelpCommand : CommandUtils() {

    override fun handle(e: MessageReceivedEvent) {
        e.textChannel.sendMessage(SampleBot.commandsEmbed
                .setAuthor(e.author.name, null, e.author.avatarUrl)
                .setFooter(e.jda.selfUser.name, e.jda.selfUser.avatarUrl)
                .setColor(randomColor())
                .build()).queue()
    }
}