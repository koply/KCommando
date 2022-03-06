package me.koply.aaa;

import me.koply.kcommando.KCommando;
import me.koply.kcommando.integration.impl.jda.JDAIntegration;
import me.koply.kcommando.internal.OptionType;
import me.koply.kcommando.internal.annotations.HandleCommand;
import me.koply.kcommando.internal.annotations.HandleButton;
import me.koply.kcommando.internal.annotations.Option;
import me.koply.kcommando.internal.annotations.HandleSlash;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;

public class Test extends ListenerAdapter {

    public static void main(String[] args) throws LoginException, InterruptedException {
        String token = System.getenv("TOKEN");
        JDA jda = JDABuilder.createDefault(token)
                .disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                .setBulkDeleteSplittingEnabled(false)
                .setActivity(Activity.watching("Ğ"))
                .build();
        jda.awaitReady();

        Test app = new Test();
        jda.addEventListener(app);

        new KCommando(new JDAIntegration(jda))
                .addPackagePath(Test.class.getPackage().getName())
                .setVerbose(true)
                .setPrefix(".")
                .setCooldown(2000)
                .build();

    }

    @HandleButton("testButton")
    public static void button(ButtonClickEvent e) {
        System.out.println("testButton from kekomandoo");
    }

    @HandleButton("zink")
    public static void zink(ButtonClickEvent e) {
        System.out.println("zink from kekomandooo");
    }

    @HandleCommand(name = "test", aliases = "test")
    public static void gommand(MessageReceivedEvent e) {
        e.getChannel().sendMessage("bişeyler yapmışsın knk").queue();
    }

    @HandleSlash(name = "test", desc = "testing slash", guildId = 674334330444709904L,
    options = @Option(type = OptionType.STRING, name = "value", required = true))
    public static void slash(SlashCommandEvent e) {
        e.deferReply(false).queue();
        OptionMapping value = e.getOption("value");
        if (value == null) System.out.println("option::value nasıl null olm?");
        String val = value.getAsString();
        e.getHook().sendMessage("Bu bir cevap mı bilmiyorum. -> " + val).queue();
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        System.out.println("slash command timez " + event.getName() + " - " + event.getCommandId());
        System.out.println(event.getOptions());

        System.out.println(event.getCommandString());
        if (!event.getName().equals("ping")) return;
        event.reply("ponk")
                .addActionRow(Button.success("testButton", Emoji.fromUnicode("🥴")),
                        Button.primary("zink", "label"))
                .queue();

    }
}
