![](http://image-write-app.herokuapp.com/?x=880&y=33&size=130&text=koply&url=https%3A%2F%2Fimage-write-app.herokuapp.com%2F%3Fx%3D45%26y%3D25%26size%3D150%26text%3DKCommando%26url%3Dhttps%3A%2F%2Fwww.afcapital.ru%2Fa%2Fpgs%2Fimages%2Fcontent-grid-bg.png)

[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/koply/KCommando.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/koply/KCommando/context:java)
[![Build Status](https://api.travis-ci.com/koply/KCommando.svg?branch=v5.0.0)](https://travis-ci.com/koply/kcommando)
[![jitpack-version](https://jitpack.io/v/koply/KCommando.svg)](https://jitpack.io/#koply/KCommando)
![LICENSE](https://img.shields.io/github/license/koply/KCommando?style=flat)

Annotation-based multifunctional command handler framework for JDA & Javacord. KCommando has a external plugin system.

## Features
All these features have a modular structure and you can edit all of these modules and integrate them for your projects.
1. [Integrations](#kcommando-integrations)
2. [Creating Slash Command](#how-to-create-a-slash-command)
3. [Handling Buttons](#how-to-handle-buttons)
4. [Creating Classic Commands](#how-to-create-a-classic-command)
5. [Parameterized Constructors](#how-to-use-parameterized-constructor)
6. [Command Features](#command-features)
   - [Command Method Types](#possible-command-methods)
   - [Args and Prefix Parameters](#properties-of-the-args-and-prefix-parameters)
7. [Cool Features](#cool-features)
   - [Command Suggestions](#how-to-use-suggested-commands)
   - [Custom Prefixes For Guilds](#how-to-use-custom-prefixes)
   - [CRON Service](#cron-service)
8. [How To Install](#how-to-install)
   - [Maven](#with-maven)
   - [Gradle](#with-gradle)
9. [Example Projects](#example-repositories)
	
# KCommando Integrations

### Integration Usage For JDA
```java
package com.example.mybot;

public class Main {

    public void main(String[] args) throws Exception {
        JDA jda = JDABuilder.createDefault("TOKEN").build();
        jda.awaitReady();
        
        JDAIntegration integration = new JDAIntegration(jda);
        
        KCommando kcommando = new KCommando(integration)
              .setOwners(00000000L, 00000000L) // varargs LONG
              .addPackage("com.example.mybot") // package to analyze
              .setCooldown(5000L) // 5 seconds as 5000 ms
              .setPrefix("!")
              .setReadBotMessages(false) // default false
              .setUseCaseSensitivity(false) // default false
              .setAllowSpacesInPrefix(true) // default false
              .setDefaultFalseMethodName("defaultCallback")
              .setVerbose(true) // for logging
              .build();
    }
}
```

That's it. Now, we can create slash commands or classic commands.

### How To Create A Slash Command
You don't have to identify the `guildId`. If you don't, it will be a global command. Also options are optional. It's okay if the method is static. 

```java
package com.example.mybot.slash;

public class MySlashCommands {
    
    @HandleSlash(name = "hello", desc = "Test command.", guildId = 000000L,
                 options = @Option(type = OptionType.STRING, name = "yourName", required = true))
    public void helloCommand(SlashCommandInteractionEvent e) {
		
        e.deferReply(false).queue();
        String name = e.getOption("yourName").getAsString();
        e.getHook().sendMessage("Hello " + name + "!").queue();
        
    }
    
    @HandleSlash(name = "ping", desc = "Pong!")
    public static void pingCommand(SlashCommandInteractionEvent e) {
        
        e.reply("Pong!!").addActionRow(
                Button.primary("buttonHello", "Button Text"),
                Button.secondary("processData", "Process")
        ).queue();
        
    }
}
```

### How To Handle Buttons
```java
package com.example.mybot.buttons;

public class Hello {
    
    @HandleButton("buttonHello")
    public void helloButton(ButtonInteractionEvent e) {
        // ...
    }
    
    @HandleButton("processData")
    public void processor(ButtonInteractionEvent e) {
        // ...
    }
    
}
```

### How To Create A Classic Command

```java
package com.example.mybot.commands;

public class BasicCommands {
    
    @HandleCommand(name = "Ping", aliases = "ping", 
                   description = "Pong!", /* "-" default */
                   /* "defaultCallback" is default because we set it to it above here */
                   falseMethod = "customFalse",
                   guildOnly = false,  /* false default */
                   ownerOnly = false,  /* false default */
                   privateOnly = false /* false default */)
    public boolean ping(MessageReceivedEvent e /*, String[] args, String prefix */) {
        e.getChannel().sendMessage("Pong!").queue();
        return false; // for test the customFalse callback method
    }
    
    @HandleCommand(name = "Test", aliases = {"test", "testo"})
    public void test(MessageReceivedEvent e, String[] args /*, String prefix */) {
        // ...
    }
    
    @HandleFalse
    public static void customFalse(MessageReceivedEvent e /*, String[] args, String prefix */) {
        e.getMessage().addReaction("⛔").queue();
    }
    
    @HandleFalse // we set it to default with method name while initializing the KCommando
    public static void defaultCallback(MessageReceivedEvent e, String[] args, String prefix) {
        // ...
    }
    
}
```

_Optionally you can use the class and handle method as *final* to reduce compile time._

Aliases field can be an array: `aliases = {"ping", "pingu"}`

## How To Use Parameterized Constructor
```java
package com.example.mybot;

public class Example {
    
    // kcommando doesn't have a database manager
    // this is example for how to use parameterized classes with kcommando
    private final DatabaseManager databaseManager;
    
    public Example(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @HandleCommand(name = "ListDatabase", aliases = {"db", "listdb"})
    public void command(MessageReceivedEvent e) {
        String example = databaseManager.query("SELECT * FROM logs");
        // ...
    } 
}
```

```java
package com.example.mybot;

public class Main {
    public void main(String[] args) throws Exception {
        JDA jda = JDABuilder.createDefault("TOKEN").build();
        jda.awaitReady();
        
        JDAIntegration integration = new JDAIntegration(jda);
        KCommando kcommando = new KCommando(integration)
                .setPackage("com.example.mybot") // package to analyze
                .setPrefix("!")
                .setVerbose(true)
                .build();

        DatabaseManager databaseManager = new DatabaseManager();
        
        // this class includes command
        // kcommando will use this instance while executing the command
        Example myObject = new Example(databaseManager);

        // also you can do this before build the kcommando
        kcommando.registerObject(myObject); // <--------
    }
}
```

# Command Features

## Possible Command Methods

You can use just one of these in your command class. Parameters will never be null. You don't need null checks.

```java
<void/boolean> commandMethod(<Event> e)
<void/boolean> commandMethod(<Event> e, String[] args)
<void/boolean> commandMethod(<Event> e, String[] args, String prefix)
```

### Properties of the *args* and *prefix* parameters
Args are splitted by the "space" characters. The 0. index is the command text itself (without the prefix).
```
Entered Command: "!ping test 123"
args[0]: "ping"
args[1]: "test"
args[2]: "123"

prefix: "!"
```
# Cool Features

## How To Use Suggested Commands

This callback will be called with the suggestions list and the event object when an incorrect command is used.
Currently, the suggestions are based on the JaroWrinklerDistance algorithm.

```java
package com.example.mybot;

public class OtherThings {
	
    // also you can use the Set<String> instead of the List<String>
    @SimilarCallback
    public static void similar(MessageReceivedEvent e, List<String> similarCommands /*, String enteredCommand */) {
        String commands = String.join(", ", similarCommands);
        e.getChannel().sendMessage("Similar commands are here: " + commands).queue();
    }

}
```

## How To Use Custom Prefixes

You can add custom prefixes for guilds.

```java
// adds a prefix for the selected guild.
Integration#addCustomPrefix(long guildID, String prefix) 

// removes a prefix for the selected guild. This method is safe to use.
Integration#removeCustomPrefix(long guildID, String prefix)

// removes all custom prefixes for selected guild. 
Integration#removeAllCustomPrefixes(long guildID) 
```

If a guild has a custom prefix, the normal prefix will be overridden for that guild but it is possible to use more than one prefix at the same time. You can remove and disable custom prefixes for the single guild.

## Cron Service
KCommando has a minute-based async CronService and you can use it.
```java
CronService.getInstance().addRunnable(() -> {
	// do stuff
}, 5); /* every 5 minutes */
```

## How To Install

To always use the latest version, you can write '-SNAPSHOT' in the version field. This use is not recommended because new versions may not always be fully backwards compatible.

### With Maven:
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>


<!-- FOR JDA -->
<dependency>
    <groupId>com.github.koply.KCommando</groupId>
    <artifactId>jda-integration</artifactId>
    <version>JITPACK-VERSION</version>
</dependency>

<!-- FOR JAVACORD -->
<dependency>
    <groupId>com.github.koply.KCommando</groupId>
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
    implementation 'com.github.koply.KCommando:jda-integration:JITPACK-VERSION'
}

// FOR JAVACORD
dependencies {
    implementation 'com.github.koply.KCommando:javacord-integration:JITPACK-VERSION'
}
```

**Please change 'JITPACK-VERSION' fields to the latest release version.**

Github packages are ignored. Please use jitpack repositories.

## Example Repositories
 | [Rae Discord Bot](https://github.com/koply/Rae)

# Don't be afraid to contribute!
