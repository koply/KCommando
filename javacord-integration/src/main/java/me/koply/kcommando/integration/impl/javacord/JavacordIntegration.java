package me.koply.kcommando.integration.impl.javacord;

import me.koply.kcommando.handler.ButtonClickHandler;
import me.koply.kcommando.handler.CommandHandler;
import me.koply.kcommando.handler.SlashCommandHandler;
import me.koply.kcommando.integration.Integration;
import me.koply.kcommando.integration.impl.javacord.listeners.ButtonListener;
import me.koply.kcommando.integration.impl.javacord.listeners.CommandListener;
import me.koply.kcommando.integration.impl.javacord.listeners.SlashListener;
import me.koply.kcommando.internal.annotations.HandleSlash;
import me.koply.kcommando.internal.annotations.Option;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.util.ArrayList;
import java.util.List;

public class JavacordIntegration extends Integration {

    public final DiscordApi api;
    public JavacordIntegration(DiscordApi api) {
        super(api.getClientId());
        this.api = api;
    }

    @Override
    public void registerCommandHandler(CommandHandler handler) {
        api.addMessageCreateListener(new CommandListener(handler));
    }

    @Override
    public void registerSlashCommandHandler(SlashCommandHandler handler) {
        api.addSlashCommandCreateListener(new SlashListener(handler));
    }

    @Override
    public void registerButtonClickHandler(ButtonClickHandler handler) {
        api.addButtonClickListener(new ButtonListener(handler));
    }

    @Override
    public void registerSlashCommand(HandleSlash info) {
        String name = info.name();
        String desc = info.desc();
        boolean isglobal = info.global();

        Option[] options = info.options();
        List<SlashCommandOption> optionList = new ArrayList<>();

        for (Option option : options) {
            String optionName = option.name();
            String optionDesc = option.desc();
            boolean req = option.required();

            SlashCommandOptionType type = SlashCommandOptionType.fromValue(option.type().value);

            //optionList.add(SlashCommandOption.createWithChoices())
            // TODO :: Javacord


        }

        if (isglobal) {
            SlashCommand.with(name, desc, optionList)
                    .createGlobal(api)
                    .join();
        } else {
            long[] guildIds = info.guildId();
            for (long guildId : guildIds) {
                api.getServerById(guildId).ifPresent((server) -> SlashCommand.with(name, desc, optionList)
                        .createForServer(server)
                        .join());
            }
        }
    }

    @Override
    public Class<?> getMessageEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Class<?> getSlashEventType() {
        return SlashCommandCreateEvent.class;
    }

    @Override
    public Class<?> getButtonEventType() {
        return ButtonClickEvent.class;
    }
}