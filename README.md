<h1 align="center"> KCommando </h1>

[![Build Status](https://travis-ci.com/musabrt/kcommando.svg?branch=master)](https://travis-ci.com/musabrt/kcommando)
![LICENSE](https://img.shields.io/github/license/MusaBrt/KCommando?style=flat)
![stars](https://img.shields.io/github/stars/MusaBrt/KCommando?style=flat)
![version](https://img.shields.io/badge/version-1.1.1-blue)
[![](https://jitpack.io/v/MusaBrt/KCommando.svg)](https://jitpack.io/#MusaBrt/KCommando)

Annotation based command handler wrapper for JDA.

## Usage
```java
public class Main {
    public void main(String[] args) {
        JDA jda = JDABuilder.createDefault("TOKEN").build();
        jda.awaitReady();
        
        KCommando kcommando = new KCommando(jda)
              .setCooldown(5000L) // 5 seconds as 5000 ms
              .setOwners("FIRST_OWNER_ID", "SECOND_OWNER_ID")
              .setPackage("com.example.mybot.commands") // command classes package path
              .setPrefix("!")
              .setReadBotMessages(false) // default false
              .build();
    }
}
```

That's it. Now, we need a command.

## How To Create A Command
```java
public class BasicCommand implements CommandUtils {

    @Command(names = "ping"
             description = "Pong!"
             guildOnly = false /* false default */
             ownerOnly = false /* false default */
             privateOnly = false /* false default */
             sync = false /* false default */)
    public void pingo(MessageReceivedEvent e) {
        e.getTextChannel().sendMessage(embed("Pong!")).queue();
    }
}
```
_Optionally you can use final class and final handle method for increase init time._

Names field is can be an array: `names = {"ping", "pingu"}`

## Java Ping-Pong Bot
```java
public class Main implements CommandUtils {
    public static void main(String[] args) {
        JDA jda = JDABuilder.createDefault("YOUR-TOKEN").setAutoReconnect(true).build();
        jda.awaitReady();

        KCommando kcommando = new KCommando(jda)
                .setPrefix(".")
                .setPackage(Main.class.getPackage().getName()).build();
    }
    
    @Command(names = "ping",
            description = "Pong!")
    public void pingCommand(MessageReceivedEvent e) {
        e.getTextChannel().sendMessage("Pong!").queue();
    }    
}
```

## Kotlin Ping-Pong Bot
```kotlin
object Main : CommandUtils {
    
    @JvmStatic
    fun main() {
        val jda = JDABuilder.createDefault("YOUR-TOKEN").setAutoReconnect(true).build()
        jda.awaitReady()
        
        val kcommando = KCommando(jda)
                .setPrefix(".")
                .setPackage(Main::class.java.`package`.name).build();
    }
    
    @Command(names = ["ping", "pingu"], 
            description = "Pong!")
    fun pinCommand(e : MessageReceivedEvent) {
        e.textChannel.sendMessage("Pong!").queue()
    }
}
```

## How To Install
### With Maven:
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.MusaBrt</groupId>
    <artifactId>KCommando</artifactId>
    <version>JITPACK-VERSION</version>
</dependency>
```
### With Gradle:
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.MusaBrt:KCommando:JITPACK-VERSION'
}
```
