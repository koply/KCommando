package me.koply.kcommando.integration.impl.javacord;

import me.koply.kcommando.KCommando;
import me.koply.kcommando.handler.ButtonClickHandler;
import me.koply.kcommando.handler.CommandHandler;
import me.koply.kcommando.handler.SlashCommandHandler;
import me.koply.kcommando.integration.Integration;
import me.koply.kcommando.integration.impl.javacord.listeners.ButtonListener;
import me.koply.kcommando.integration.impl.javacord.listeners.CommandListener;
import me.koply.kcommando.integration.impl.javacord.listeners.SlashListener;
import me.koply.kcommando.internal.Kogger;
import me.koply.kcommando.internal.OptionType;
import me.koply.kcommando.internal.annotations.HandleSlash;
import me.koply.kcommando.internal.annotations.Option;
import me.koply.kcommando.internal.boxes.SlashBox;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public void registerSlashCommand(SlashBox box) {
        HandleSlash info = box.info;

        String name = info.name();
        String desc = info.desc();

        Option[] options = info.options();
        List<SlashCommandOption> optionList = new ArrayList<>();

        for (Option option : options) {
            if (option.type() == OptionType.UNKNOWN) continue;

            String optionName = option.name();
            String optionDesc = option.desc();
            boolean req = option.required();

            SlashCommandOptionType type = SlashCommandOptionType.fromValue(option.type().value);
            SlashCommandOption javacordOption = SlashCommandOption.createWithChoices(type, optionName, optionDesc, req);

            optionList.add(javacordOption);
        }

        SlashCommandBuilder builder = SlashCommand.with(name, desc, optionList)
                .setEnabledInDms(info.enabledInDms());

        box.getPerm().ifPresent(perm -> builder.setDefaultEnabledForPermissions(Util.getPermissions(perm.value())));

        if (info.global()) {
            builder.createGlobal(api).join();
            return;
        }

        long[] guildIds = info.guildId();
        if (guildIds[0] == 0) {
            if (KCommando.verbose) {
                Kogger.warn("The Slash Command named as " + name + " is not global. At the same time it doesn't have guildId. This command cannot be register to Discord.");
            }
        } else for (long guildId : guildIds) {
            Optional<Server> server =  api.getServerById(guildId);
            if (server.isPresent()) {
                builder.createForServer(server.get()).join();
            } else if (KCommando.verbose) {
                Kogger.warn("Guild not found for Slash Command named as " + name);
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