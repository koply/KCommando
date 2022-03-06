package me.koply.kcommando.integration.impl.jda;

import me.koply.kcommando.KCommando;
import me.koply.kcommando.handler.ButtonClickHandler;
import me.koply.kcommando.handler.CommandHandler;
import me.koply.kcommando.handler.SlashCommandHandler;
import me.koply.kcommando.integration.Integration;
import me.koply.kcommando.integration.impl.jda.listeners.ButtonListener;
import me.koply.kcommando.integration.impl.jda.listeners.CommandListener;
import me.koply.kcommando.integration.impl.jda.listeners.SlashListener;
import me.koply.kcommando.internal.Kogger;
import me.koply.kcommando.internal.annotations.Option;
import me.koply.kcommando.internal.annotations.HandleSlash;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class JDAIntegration extends Integration {

    public final JDA api;
    public JDAIntegration(JDA api) {
        super(api.getSelfUser().getIdLong());
        this.api = api;
    }

    public void registerCommandHandler(CommandHandler handler) {
        api.addEventListener(new CommandListener(handler));
    }

    @Override
    public void registerSlashCommandHandler(SlashCommandHandler handler) {
        api.addEventListener(new SlashListener(handler));
    }

    @Override
    public void registerButtonClickHandler(ButtonClickHandler handler) {
        api.addEventListener(new ButtonListener(handler));
    }

    @Override
    public void registerSlashCommand(HandleSlash info) {
        String name = info.name();
        String desc = info.desc();
        boolean isglobal = info.global();

        Option[] options = info.options();
        OptionData[] optionDatas = new OptionData[options.length];
        for (int i = 0; i < options.length; i++) {
            OptionType type = OptionType.fromKey(options[i].type().value);
            optionDatas[i] = new OptionData(type, options[i].name(), options[i].desc(), options[i].required());
        }
        if (isglobal) {
            api.upsertCommand(name, desc).addOptions(optionDatas).queue();
            return;
        }

        long[] guildIds = info.guildId();
        if (guildIds[0] == 0) {
            if (KCommando.verbose) {
                Kogger.warn("The Slash Command named as " + name + " is not global. At the same time it doesn't have guildId. This command cannot be register to Discord.");
            }
        } else for (long guildId : guildIds) {
            Guild guild = api.getGuildById(guildId);
            if (guild == null) {
                if (KCommando.verbose) {
                    Kogger.warn("Guild not found for Slash Command named as " + name);
                }
            } else {
                guild.upsertCommand(name, desc).addOptions(optionDatas).queue();
            }
        }
    }

    @Override
    public Class<?> getMessageEventType() {
        return MessageReceivedEvent.class;
    }

    @Override
    public Class<?> getSlashEventType() {
        return SlashCommandEvent.class;
    }

    @Override
    public Class<?> getButtonEventType() {
        return ButtonClickEvent.class;
    }
}