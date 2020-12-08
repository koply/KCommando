![](http://image-write-app.herokuapp.com/?x=880&y=33&size=130&text=koply&url=https%3A%2F%2Fimage-write-app.herokuapp.com%2F%3Fx%3D45%26y%3D25%26size%3D150%26text%3DKCommando%26url%3Dhttps%3A%2F%2Fwww.afcapital.ru%2Fa%2Fpgs%2Fimages%2Fcontent-grid-bg.png)

[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/MusaBrt/KCommando.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/MusaBrt/KCommando/context:java)
[![Build Status](https://travis-ci.com/musabrt/kcommando.svg?branch=master)](https://travis-ci.com/musabrt/kcommando)
[![jitpack-version](https://jitpack.io/v/MusaBrt/KCommando.svg)](https://jitpack.io/#MusaBrt/KCommando)
![LICENSE](https://img.shields.io/github/license/MusaBrt/KCommando?style=flat)

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
              .setCaseSensivity(false) // default false
              .build();
    }
}
```

That's it. Now, we need a command.

## How To Create A Command
```java
@Commando(name = "Ping!"
           aliases = "ping",
           description = "Pong!", /* "-" default */
           guildOnly = false, /* false default */
           ownerOnly = false, /* false default */
           privateOnly = false, /* false default */
           sync = false /* false default */)
public class BasicCommand extends Command {
    
    public BasicCommand() {
        // when handle method returns false, runs the declared callback like this
        getInfo().setOnFalseCallback(e -> e.getMessage().addReaction("⛔").queue());
    }

    @Override
    public boolean handle(MessageReceivedEvent e /* optionally you can use the Params parameter*/) {
        e.getTextChannel().sendMessage(Utils.embed("Pong!")).queue();
        return true;
        // if your command is completed successfully, you must return "true"
    }
}
```
_Optionally you can use final class and final handle method for decrease init time._

Aliases field is can be an array: `aliases = {"ping", "pingu"}`

## Possible Handle Methods

You can use just one in your command class. Parameters are annotated with @NotNull. You don't need to null check.

```java
public boolean handle(MessageReceivedEvent e) // CommandType.EVENT -> 0x01
public boolean handle(MessageReceivedEvent e, String[] args)  // CommandType.ARGNEVENT -> 0x02
```

### Java Ping-Pong Bot
```java

@Commando(name = "Ping!",
           aliases = {"ping", "pingu"},
           description = "Pong!")
public class Main extends Command {
    public static void main(String[] args) {
        JDA jda = JDABuilder.createDefault("YOUR-TOKEN").setAutoReconnect(true).build();
        jda.awaitReady();

        KCommando kcommando = new KCommando(jda)
                .setPrefix(".")
                .setPackage(Main.class.getPackage().getName()).build();
    }
    
    @Override
    public boolean handle(MessageReceivedEvent e) {
        e.getTextChannel().sendMessage("Pong!").queue();
        return true;
    }    
}
```

### Kotlin Ping-Pong Bot
```kotlin
@Commando(name = "Ping!",
           aliases = ["ping", "pingu"], 
           description = "Pong!")
class Main : Command() {
    
    @JvmStatic
    fun main() {
        val jda = JDABuilder.createDefault("YOUR-TOKEN").setAutoReconnect(true).build()
        jda.awaitReady()
        
        val kcommando = KCommando(jda)
                .setPrefix(".")
                .setPackage(Main::class.java.`package`.name).build();
    }
    
    @Override
    fun handle(e : MessageReceivedEvent) : Boolean {
        e.textChannel.sendMessage("Pong!").queue()
        return true
    }
}
```

### Detailed Samples
Java -> [Sample Pom.xml](https://github.com/MusaBrt/KCommando/blob/master/java-sample/pom.xml) - [Sample Help System](https://github.com/MusaBrt/KCommando/blob/master/java-sample/src/me/koply/javasample/SampleBot.java)

Kotlin -> [Sample Pom.xml](https://github.com/MusaBrt/KCommando/blob/master/kotlin-sample/pom.xml) - [Sample Help System](https://github.com/MusaBrt/KCommando/blob/master/kotlin-sample/src/me/koply/kotlinsample/SampleBot.kt)

## How To Install

To always use the latest version, you can write 'master-SNAPSHOT' in the version field.

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

Github packages are ignored. Please use jitpack repositories.
