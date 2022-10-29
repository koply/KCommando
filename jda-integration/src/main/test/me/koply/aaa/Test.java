package me.koply.aaa;

import me.koply.kcommando.KCommando;
import me.koply.kcommando.integration.impl.jda.JDAIntegration;
import me.koply.kcommando.internal.OptionType;
import me.koply.kcommando.internal.annotations.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Test extends ListenerAdapter {

    public static void main(String[] args) throws InterruptedException {
        String token = System.getenv("TOKEN");
        JDA jda = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
                .disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                .setBulkDeleteSplittingEnabled(false)
                .setActivity(Activity.watching("Ğ"))
                .build();
        jda.awaitReady();

        Test app = new Test();
        jda.addEventListener(app);

        new KCommando(new JDAIntegration(jda))
                .addPackage(Test.class.getPackage().getName())
                .setVerbose(true)
                .setPrefix(".")
                .setOwners(269140308208517130L)
                .setCooldown(2000)
                .setUseCaseSensitivity(false)
                .setReadBotMessages(false)
                .setAllowSpacesInPrefix(true)
                .setDefaultFalseMethodName("defaultFalse")
                .build();

    }

    @HandleFalse
    public static void defaultFalse(MessageReceivedEvent e) {
        long ms = System.currentTimeMillis();
        e.getChannel().sendMessage("Hello from handlefalse but thats not a good way to send that msg " + ms).queue();
    }

    @SimilarCallback
    public static void similar(MessageReceivedEvent e, List<String> similarCommands) {
        String commands = String.join(", ", similarCommands);
        e.getChannel().sendMessage("Similar commands are here: " + commands).queue();
    }

    @HandleButton("testButton")
    public static void button(ButtonInteractionEvent e) {
        System.out.println("testButton from kekomandoo");
    }

    @HandleButton("zink")
    public static void zink(ButtonInteractionEvent e) {
        System.out.println("zink from kekomandooo");
    }

    @HandleCommand(name = "test", aliases = "test")
    public static void gommand(MessageReceivedEvent e) {
        e.getChannel().sendMessage("bişeyler yapmışsın knk").queue();
    }

    @HandleSlash(name = "test", desc = "testing slash", guildId = 674334330444709904L,
    options = @Option(type = OptionType.STRING, name = "value", required = true))
    public static void slash(SlashCommandInteractionEvent e) {
        e.deferReply(false).queue();
        OptionMapping value = e.getOption("value");
        if (value == null) System.out.println("option::value nasıl null olm?");
        String val = value.getAsString();
        e.getHook().sendMessage("Bu bir cevap mı bilmiyorum. -> " + val).queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        System.out.println("slash command timez " + event.getName() + " - " + event.getCommandId());
        System.out.println(event.getOptions());

        System.out.println(event.getCommandString());
        if (!event.getName().equals("ping")) return;
        event.reply("ponk")
                .addActionRow(Button.success("testButton", Emoji.fromUnicode("🥴")),
                        Button.primary("zink", "label"))
                .queue();
    }

    @HandleCommand(name = "private", aliases="private", privateOnly = true)
    public void privateCommand(MessageReceivedEvent e) {
        e.getChannel().sendMessage("i think thats done my friend").queue();
    }

    @HandleCommand(name = "owner", aliases ="owner", ownerOnly = true)
    public void ownerCommand(MessageReceivedEvent e) {
        e.getChannel().sendMessage("thats done too").queue();
    }

    @HandleCommand(name = "guild", aliases="guild", guildOnly = true)
    public void guildCommand(MessageReceivedEvent e) {
        e.getChannel().sendMessage("watchudoing").queue();
    }

    @HandleCommand(name = "false", aliases="false")
    public boolean falsem(MessageReceivedEvent e) {
        long ms = System.currentTimeMillis();
        e.getChannel().sendMessage("normal method sa " + ms).queue();
        return false;
    }

    @HandleFalse
    public void anotherFalse(MessageReceivedEvent e) {
        e.getChannel().sendMessage("Hi from anotherFalse").queue();
    }

    @HandleCommand(name = "customfalse", aliases = "cfalse", falseMethod = "anotherFalse")
    public boolean customfalse(MessageReceivedEvent e) {
        return false;
    }

    public void pingCommand(MessageReceivedEvent e) {

    }
}
