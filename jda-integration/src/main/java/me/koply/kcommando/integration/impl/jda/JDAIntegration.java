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
import me.koply.kcommando.internal.annotations.HandleSlash;
import me.koply.kcommando.internal.annotations.Option;
import me.koply.kcommando.internal.boxes.SlashBox;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

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
    public void registerSlashCommand(SlashBox box) {
        HandleSlash info = box.info;

        String name = info.name();
        String desc = info.desc();
        boolean isglobal = info.global();

        Option[] options = info.options();
        OptionData[] optionDatas = new OptionData[options.length];
        for (int i = 0; i < options.length; i++) {
            if (options[i].type() == me.koply.kcommando.internal.OptionType.UNKNOWN) continue;
            OptionType type = OptionType.fromKey(options[i].type().value);
            optionDatas[i] = new OptionData(type, options[i].name(), options[i].desc(), options[i].required());
        }

        boolean guildOnly = !info.enabledInDms();

        CommandData data = new CommandDataImpl(name, desc)
                .addOptions(optionDatas)
                .setGuildOnly(guildOnly);

        box.getPerm().ifPresent(perm -> data.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Util.getPermissions(perm.value()))));

        if (isglobal) {
            api.upsertCommand(data).queue();
            return;
        }

        long[] guildIds = info.guildId();
        if (guildIds[0] == 0) {
            if (KCommando.verbose) {
                Kogger.warn("The Slash Command named as " + name + " is not global. At the same time it doesn't have guildId. This command cannot be register to Discord.");
            }
        } else for (long guildId : guildIds) {
            Guild guild = api.getGuildById(guildId);
            if (guild != null) {
                guild.upsertCommand(data).queue();
            } if (KCommando.verbose) {
                Kogger.warn("Guild not found for Slash Command named as " + name);
            }
        }
    }

    @Override
    public Class<?> getMessageEventType() {
        return MessageReceivedEvent.class;
    }

    @Override
    public Class<?> getSlashEventType() {
        return SlashCommandInteractionEvent.class;
    }

    @Override
    public Class<?> getButtonEventType() {
        return ButtonInteractionEvent.class;
    }
}