![](http://image-write-app.herokuapp.com/?x=880&y=33&size=130&text=koply&url=https%3A%2F%2Fimage-write-app.herokuapp.com%2F%3Fx%3D45%26y%3D25%26size%3D150%26text%3DKCommando%26url%3Dhttps%3A%2F%2Fwww.afcapital.ru%2Fa%2Fpgs%2Fimages%2Fcontent-grid-bg.png)

[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/MusaBrt/KCommando.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/MusaBrt/KCommando/context:java)
[![Build Status](https://travis-ci.com/musabrt/kcommando.svg?branch=master)](https://travis-ci.com/musabrt/kcommando)
[![jitpack-version](https://jitpack.io/v/MusaBrt/KCommando.svg)](https://jitpack.io/#MusaBrt/KCommando)
![LICENSE](https://img.shields.io/github/license/MusaBrt/KCommando?style=flat)

Annotation based command handler framework for JDA & Javacord.

## Features
1. [Integrations](#kcommando-integrations)
2. [JDA Section](#integration-usage-for-jda)
3. [Javacord Section](#javacord-section)
4. [Handle Methods](#possible-command-handle-methods)
5. [Cool Features](#cool-features)
	- [Custom Prefixes](#how-to-use-custom-prefixes)
	- [Blacklist User](#blacklist-user)
	- [Blacklist Member](#blacklist-member)
	- [Blacklist Channel](#blacklist-channel)
	- [Callback For Blacklisted Usage](#callback-for-blacklisted-usages)
6. [Install](#how-to-install)
	- [Maven](#with-maven)
	- [Gradle](#with-gradle)
	
# KCommando Integrations

### Integration Usage For JDA
```java
public class Main extends JDAIntegration {
    
    public Main(JDA jda) { super(jda); }

    public void main(String[] args) throws Exception {
        JDA jda = JDABuilder.createDefault("TOKEN").build();
        jda.awaitReady();
        
        KCommando kcommando = new KCommando(new Main(jda))
              .setCooldown(5000L) // 5 seconds as 5000 ms
              .setOwners("FIRST_OWNER_ID", "SECOND_OWNER_ID")
              .setPackage("com.example.mybot.commands") // command classes package path
              .setPrefix("!")
              .setReadBotMessages(false) // default false
              .setCaseSensivity(Locale.getDefault()) // Optional<Locale> -> default false
              .build();
    }
}
```

That's it. Now, we need a command.

### How To Create A Command For JDA
```java
@Commando(name = "Ping!"
           aliases = "ping",
           description = "Pong!", /* "-" default */
           guildOnly = false, /* false default */
           ownerOnly = false, /* false default */
           privateOnly = false, /* false default */
           sync = false /* false default */)
public class BasicCommand extends JDACommand {
    
    public BasicCommand() {
        // when handle method returns false, runs the declared callback like this
        getInfo().setOnFalseCallback( (KRunnable<MessageReceivedEvent>) e -> e.getMessage().addReaction("⛔").queue() );
    }

    @Override
    public boolean handle(MessageReceivedEvent e /* optionally String[] args*/ ) {
        e.getChannel().sendMessage( "Pong!" ).queue();
        return true;
        // if your command is completed successfully, you must return "true"
    }
}
```

_Optionally you can use final class and final handle method for decrease init time._

Aliases field is can be an array: `aliases = {"ping", "pingu"}`

## Javacord Section

### Integration Usage For Javacord
```java
public class Main extends JavacordIntegration {
    
    public Main(DiscordApi discordApi) { super(discordApi); }

    public void main(String[] args) {
        DiscordApi discordApi = new DiscordApiBuilder().setToken(token)
            .login().join();
        
        KCommando kcommando = new KCommando(new Main(discordApi))
              .setPackage("com.example.mybot.commands") // command classes package path
              .setPrefix("!")
              .build();
    }
}
```

### How To Create A Command For Javacord
```java
@Commando(name = "Ping!"
           aliases = "ping")
public class BasicCommand extends JavacordCommand {
    
    public BasicCommand() {
        // when handle method returns false, runs the declared callback like this
        getInfo().setOnFalseCallback( (KRunnable<MessageCreateEvent>) e -> e.getMessage().addReaction("⛔") );
    }

    @Override
    public boolean handle(MessageCreateEvent e /* optionally String[] args*/ ) {
        e.getChannel().sendMessage( "Pong!" );
        return true;
        // if your command is completed successfully, you must return "true"
    }
}
```

## Possible Command Handle Methods

You can use just one in your command class. Parameters cannot be empty. You don't need to null check.

```java
boolean handle(<EventFromApiWrapper> e) // CommandType.EVENT -> 0x01
boolean handle(<EventFromApiWrapper> e, String[] args)  // CommandType.ARGNEVENT -> 0x02
boolean handle(<EventFromApiWrapper> e, String[] args, String prefix)  // CommandType.PREFIXED -> 0x03
```



## How To Use Custom Prefixes

You can add custom prefixes for guilds.

```java
Integration#addCustomPrefix(long guildID, String prefix) // adds a prefix for selected guild.
Integration#removeCustomPrefix(long guildID, String prefix) // removes a prefix for selected guild. This method is safe to use.
Integration#disableCustomPrefix(long guildID) // disables all custom prefixes for selected guild.
```

If a guild has a custom prefix, the normal prefix will be unavailable on that server, but will be able to use multiprefix at the same time. You can remove and disable custom prefixes for single guild.



## How To Use Blacklist

I prefer use a static instance of subclass of Integration. You can see tests of jda and javacord integrations.

### Blacklist User
```java
Integration#getBlacklistedUsers().add(userID) // blocks selected user from all commands in the bot.
Integration#getBlacklistedUsers().remove(userID) // unblocks selected user.
```

### Blacklist Member
```java
Integration#getBlacklistedMembers() // returns all blacklisted members with guilds. (guildID, set of the blacklisted members)
Integration#getBlacklistedMembers(long guildID) // returns all blacklisted members in selected guild.
```

### Blacklist Channel
```java
Integration#getBlacklistedChannels() // returns all blacklisted channels with guilds. (guildID, set of the blacklisted channels)
Integration#getBlacklistedChannels(long guildID) // returns all blacklisted channels in selected guild.
```

### Callback For Blacklisted Usages

When a command declined due to blacklist, runs this callback.
```java
Integration#setBlacklistCallback( (JRunnable) e -> e.getMessage().addReaction("⛔") )
```

## How To Install

To always use the latest version, you can write '-SNAPSHOT' in the version field. This use is not recommended.

### With Maven:
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>


<!-- FOR JDA -->
<dependency>
    <groupId>com.github.MusaBrt.KCommando</groupId>
	<artifactId>jda-integration</artifactId>
    <version>JITPACK-VERSION</version>
</dependency>

<!-- FOR JAVACORD -->
<dependency>
    <groupId>com.github.MusaBrt.KCommando</groupId>
    <artifactId>javacord-integration</artifactId>
    <version>JITPACK-VERSION</version>
</dependency>
```
### With Gradle:
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

// FOR JDA
dependencies {
    implementation 'com.github.MusaBrt.KCommando:jda-integration:JITPACK-VERSION'
}

// FOR JAVACORD
dependencies {
    implementation 'com.github.MusaBrt.KCommando:javacord-integration:JITPACK-VERSION'
}
```

**Please change 'JITPACK-VERSION' fields to latest release version.**

Github packages are ignored. Please use jitpack repositories.

> Example Repositories
 | [Rae Discord Bot](https://github.com/MusaBrt/Rae)
