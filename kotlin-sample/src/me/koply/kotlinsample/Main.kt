package me.koply.kotlinsample

import java.util.*

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        println("Token: ")
        val scanner = Scanner(System.`in`)
        SampleBot(scanner.nextLine()).run()
    }
}