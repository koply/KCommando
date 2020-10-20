<h1 align="center"> KCommando </h1>

[![Build Status](https://travis-ci.com/musabrt/kcommando.svg?branch=master)](https://travis-ci.com/musabrt/kcommando)
![LICENSE](https://img.shields.io/github/license/MusaBrt/KCommando?style=flat)
![stars](https://img.shields.io/github/stars/MusaBrt/KCommando?style=flat)
![version](https://img.shields.io/badge/version-1.1.1-blue)

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

## How To Install
### With Maven:
```maven
<repository>
    <id>github</id>
    <name>GitHub MusaBrt Apache Maven Packages</name>
    <url>https://maven.pkg.github.com/MusaBrt/KCommando</url>
    <releases><enabled>true</enabled></releases>
    <snapshots><enabled>true</enabled></snapshots>
</repository>

<dependency>
    <groupId>me.koply</groupId>
    <artifactId>kcommando</artifactId>
    <version>1.1.1</version>
</dependency>
```
