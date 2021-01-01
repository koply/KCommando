![](http://image-write-app.herokuapp.com/?x=880&y=33&size=130&text=koply&url=https%3A%2F%2Fimage-write-app.herokuapp.com%2F%3Fx%3D45%26y%3D25%26size%3D150%26text%3DKCommando%26url%3Dhttps%3A%2F%2Fwww.afcapital.ru%2Fa%2Fpgs%2Fimages%2Fcontent-grid-bg.png)

[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/MusaBrt/KCommando.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/MusaBrt/KCommando/context:java)
[![Build Status](https://travis-ci.com/musabrt/kcommando.svg?branch=master)](https://travis-ci.com/musabrt/kcommando)
[![jitpack-version](https://jitpack.io/v/MusaBrt/KCommando.svg)](https://jitpack.io/#MusaBrt/KCommando)
![LICENSE](https://img.shields.io/github/license/MusaBrt/KCommando?style=flat)

Annotation-based multifunctional command handler framework for JDA & Javacord.

## Features
1. [Integrations](#kcommando-integrations)
2. [JDA Section](#integration-usage-for-jda)
3. [Javacord Section](#javacord-section)
4. [Command Features](#command-features)
	- [Possible Handle Methods](#possible-handle-methods)
	- [Command Callbacks](#command-callbacks) *(onFalse, ownerOnly, guildOnly, privateOnly, cooldown)*
5. [Cool Features](#cool-features)
	- [Suggested Commands](#how-to-use-suggested-commands)
	- [Custom Prefixes](#how-to-use-custom-prefixes)
	- [Blacklist User](#blacklist-user)
	- [Blacklist Member](#blacklist-member)
	- [Blacklist Channel](#blacklist-channel)
	- [Callback For Blacklisted Usage](#callback-for-blacklisted-usages)
6. [Install](#how-to-install)
	- [Maven](#with-maven)
	- [Gradle](#with-gradle)
7. [Example Repositories](#example-repositories)
	
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

# Command Features

## Possible Handle Methods

You can use just one in your command class. Parameters cannot be empty. You don't need to null check.

```java
boolean handle(<Event> e) // CommandType.EVENT -> 0x01
boolean handle(<Event> e, String[] args)  // CommandType.ARGNEVENT -> 0x02
boolean handle(<Event> e, String[] args, String prefix)  // CommandType.PREFIXED -> 0x03
```

## Command Callbacks
**Note:** All lines must be inside the constructor of your command.

#### On False Callback: This callback is run when the command returns false.
```java
getInfo().setOnFalseCallback( (JRunnable) e -> e.getMessage().addReaction("⛔") );
```

#### Owner Only Callback: This callback is run when the command for the bot owner is used by a normal user.
```java
getInfo().setOwnerOnlyCallback( (JRunnable) e ->  e.getMessage().addReaction("⛔") );
```

#### Guild Only Callback: This callback is run when the command for guild in the private message is used.
```java
getInfo().setGuildOnlyCallback( (JRunnable) e ->  e.getMessage().addReaction("⛔") );
```

#### Private Only Callback: This callback is run when the command for private conversations in the guild is used.
```java
getInfo().setPrivateOnlyCallback( (JRunnable) e ->  e.getMessage().addReaction("⛔") );
```

#### Cooldown Callback: This callback is run when the command declined due to cooldown.
```java
getInfo().setCooldownCallback( (JRunnable) e ->  e.getMessage().addReaction("⛔") );
```

# Cool Features

## How To Use Suggested Commands

Runs this callback with the similar commands list and the event object when an incorrect command is used. You must change the `**event**` part according to the API you use.

```java
Integration#setSuggestionsCallback((SuggestionsCallback<**Event**>) (e,suggestions) -> {
	if (suggestions.isEmpty()) {
		// no commands found
		return;
	}
	StringBuilder sb = new StringBuilder();
	for (CommandInfo info : suggestions) {
		sb.append( Arrays.toString(info.getAliases()) ).append(" - ");
	}
	e.getChannel().sendMessage("Last command is not found. Suggestions: \n"+sb.toString()).queue();
	});
```

## How To Use Custom Prefixes

You can add custom prefixes for guilds.

```java
Integration#addCustomPrefix(long guildID, String prefix) // adds a prefix for the selected guild.
Integration#removeCustomPrefix(long guildID, String prefix) // removes a prefix for the selected guild. This method is safe to use.
Integration#disableCustomPrefix(long guildID) // disables all custom prefixes for selected guild.
```

If a guild has a custom prefix, the normal prefix will be unavailable on that guild but will be able to use more than one prefixes at the same time. You can remove and disable custom prefixes for the single guild.



## How To Use Blacklist

I prefer to use a static instance of a subclass of Integration. You can see tests of jda and javacord integrations.

### Blacklist User
```java
Integration#getBlacklistedUsers().add(long userID) // blocks selected user from all commands in the bot.
Integration#getBlacklistedUsers().remove(long userID) // unblocks selected user.
```

### Blacklist Member
```java
Integration#getBlacklistedMembers() // returns all blacklisted members with guilds. (guildID, the set of the blacklisted members)
Integration#getBlacklistedMembers(long guildID) // returns all blacklisted members in the selected guild.
```

### Blacklist Channel
```java
Integration#getBlacklistedChannels() // returns all blacklisted channels with guilds. (guildID, the set of the blacklisted channels)
Integration#getBlacklistedChannels(long guildID) // returns all blacklisted channels in the selected guild.
```

### Callback For Blacklisted Usages

When a command declined due to a blacklist, runs this callback.
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

**Please change 'JITPACK-VERSION' fields to the latest release version.**

Github packages are ignored. Please use jitpack repositories.

## Example Repositories
 | [Rae Discord Bot](https://github.com/MusaBrt/Rae)
