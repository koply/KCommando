package me.koply.kotlinsample

import me.koply.kcommando.CommandToRun
import me.koply.kcommando.KCommando
import me.koply.kotlinsample.commands.SimpleCommands
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDABuilder

class SampleBot(private val token : String) {

    companion object {
        const val PREFIX = "."
        val commandsEmbed = EmbedBuilder()
    }

    fun run() {
        val jda = JDABuilder.createDefault(token).setAutoReconnect(true).build()
        jda.awaitReady()

        val kcommando = KCommando(jda)
                .setPrefix(PREFIX)
                .setPackage(SimpleCommands::class.java.`package`.name)
                .setOwners("269140308208517130")
                .setCooldown(5000L).build()

        initHelpEmbed(kcommando.params.commandMethods)
    }

    private fun initHelpEmbed(map : HashMap<String, CommandToRun>) {
        val set = HashSet<CommandToRun>()
        val sb = StringBuilder()
        for ((k,v) in map) {
            if (set.contains(v)) continue
            sb.append("`").append(k).append("` -> ").append(v.commandAnnotation.description).append("\n")
            set.add(v)
        }
        commandsEmbed.addField("❯ Komutlar", sb.toString(), false)
                .addField("❯ Linkler", "[KCommando](https://github.com/MusaBrt/KCommando)", false)

    }
}