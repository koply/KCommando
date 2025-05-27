package me.koply.kcommando.integration.impl.jda;

import me.koply.kcommando.KCommando;
import me.koply.kcommando.handler.ButtonClickHandler;
import me.koply.kcommando.handler.CommandHandler;
import me.koply.kcommando.handler.SlashCommandHandler;
import me.koply.kcommando.integration.Integration;
import me.koply.kcommando.integration.impl.jda.listeners.ButtonListener;
import me.koply.kcommando.integration.impl.jda.listeners.CommandListener;
import me.koply.kcommando.integration.impl.jda.listeners.SlashListener;
import me.koply.kcommando.internal.DefaultConstants;
import me.koply.kcommando.internal.Kogger;
import me.koply.kcommando.internal.annotations.Choice;
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
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class JDAIntegration extends Integration {

    public final JDA api;
    private final List<CommandDataImpl> registeredSubCommandGroups = new ArrayList<>();

    public JDAIntegration(JDA api) {
        super(api.getSelfUser().getIdLong());
        this.api = api;
    }

    @Override
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

    public void addSubCommandGroup(CommandDataImpl group) {
        registeredSubCommandGroups.add(group);
    }

    @Override
    public void registerSlashCommand(SlashBox box) {
        HandleSlash info = box.info;

        String name = info.name();
        String desc = info.desc();
        boolean isglobal = info.global();
        boolean subCommand = info.subCommand();
        String parent = info.parentGroup();

        Option[] options = info.options();
        OptionData[] optionDatas = new OptionData[options.length];
        int filledDatas = 0;
        for (Option option : options) {
            if (options[filledDatas].type() == me.koply.kcommando.internal.OptionType.UNKNOWN) continue;
            OptionType type = OptionType.fromKey(option.type().value);
            optionDatas[filledDatas] = new OptionData(type, option.name(), option.desc(), option.required());

            Choice[] choices = option.choices();

            for (Choice choice : choices) {
                // NOT EQUALS, WE NEED TO CHECK OBJECT EQUALITY
                if (DefaultConstants.DEFAULT_TEXT == choice.name() &&
                        DefaultConstants.DEFAULT_TEXT == choice.value()) {
                    continue;
                }

                optionDatas[filledDatas].addChoice(choice.name(), choice.value());
            }

            filledDatas++;
        }
        boolean isNeededToCopy = filledDatas != optionDatas.length;
        OptionData[] rolledOptionDatas = isNeededToCopy ? new OptionData[filledDatas] : optionDatas;

        if (isNeededToCopy) {
            System.arraycopy(optionDatas, 0, rolledOptionDatas, 0, filledDatas);
        }

        boolean guildOnly = !info.enabledInDms();

        CommandData commandData;
        SubcommandData subcommandData;

        if (subCommand) {
            Optional<CommandDataImpl> registeredGroup = registeredSubCommandGroups.stream()
                    .filter(registered -> registered.getName().equals(parent))
                    .findFirst();

            if (registeredGroup.isPresent()) {
                CommandDataImpl found = registeredGroup.get();

                subcommandData = new SubcommandData(name, desc);
                found.addSubcommands(subcommandData);

                commandData = found;
            } else {
                throw new NoSuchElementException("No group with that name is registered. Please register that group first!");
            }
        } else {
            commandData = new CommandDataImpl(name, desc)
                    .setGuildOnly(guildOnly)
                    .addOptions(rolledOptionDatas);
        }

        box.getPerm().ifPresent(perm ->
                commandData.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Util.getPermissions(perm.value())))
        );

        long[] guildIds = info.guildId();
        if (guildIds[0] == 0) {
            if (KCommando.verbose)
                Kogger.info("The SlashCommand that named as '" + name + "' is upserted as global command.");
            api.upsertCommand(commandData).queue();
        } else for (long guildId : guildIds) {
            Guild guild = api.getGuildById(guildId);
            if (guild != null) {
                guild.upsertCommand(commandData).queue();
                if (KCommando.verbose) {
                    Kogger.info("The SlashCommand that named as '" + name + "' is upserted as a guild command for guild '" + guildId + "'");
                }
            } else {
                if (KCommando.verbose) {
                    Kogger.warn("Guild not found for Slash Command named as " + name);
                }
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