package me.koply.kotlinsample

import me.koply.kcommando.KCommando
import me.koply.kotlinsample.commands.SimpleCommands
import net.dv8tion.jda.api.JDABuilder

class SampleBot(private val token : String) {

    fun run() {
        val jda = JDABuilder.createDefault(token).setAutoReconnect(true).build()
        jda.awaitReady()

        val kcommando = KCommando(jda)
                .setPrefix(".")
                .setPackage(SimpleCommands::class.java.`package`.name)
                .setOwners("269140308208517130")
                .setCooldown(5000L).build()
    }

}