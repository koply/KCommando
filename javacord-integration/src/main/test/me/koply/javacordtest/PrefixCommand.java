package me.koply.javacordtest;

import me.koply.kcommando.integration.impl.javacord.JRunnable;
import me.koply.kcommando.integration.impl.javacord.JavacordCommand;
import me.koply.kcommando.internal.annotations.Argument;
import me.koply.kcommando.internal.annotations.Commando;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

@Commando(name="Prefix Selector",
        aliases="prefix",
        guildOnly = true,
        onlyArguments = true)
public class PrefixCommand extends JavacordCommand {

    private final static Test testInstance = Test.getInstance();

    public PrefixCommand() {
        getInfo().setGuildOnlyCallback((JRunnable) e -> e.getMessage().addReaction("⛔"));
        getInfo().setOnFalseCallback((JRunnable) e -> e.getChannel().sendMessage("Usage: prefix <add|remove|list> <prefix>"));
    }

    @Override
    public boolean handle(MessageCreateEvent e, String[] args) {
        return false;
    }

    @Argument(arg = "add")
    public boolean add(MessageCreateEvent e, String[] args) {
        if (args.length < 3) return false;
        testInstance.addCustomPrefix(e.getServer().get().getId(), args[2]);
        sendInfo(e, args[2], true);
        return true;
    }

    @Argument(arg = "remove")
    public boolean remove(MessageCreateEvent e, String[] args) {
        if (args.length < 3) return false;
        testInstance.removeCustomPrefix(e.getServer().get().getId(), args[2]);
        sendInfo(e, args[2], false);
        return true;
    }

    @Argument(arg = "list")
    public boolean list(MessageCreateEvent e, String[] args) {
        if (testInstance.getCustomGuildPrefixes().containsKey(e.getServer().get().getId())) {
            StringBuilder sb = new StringBuilder();
            for (String s : testInstance.getCustomGuildPrefixes().get(e.getServer().get().getId())) {
                sb.append("`").append(s).append("` - ");
            }
            e.getChannel().sendMessage(new EmbedBuilder()
                    .setDescription("The prefixes are: " + sb.toString()));
        } else e.getChannel().sendMessage(new EmbedBuilder()
                .setDescription("This guild doesn't have any custom prefixes."));
        return true;
    }

    private void sendInfo(MessageCreateEvent e, String prefix, boolean isAdded) {
        final String text = isAdded ? "The '"+prefix+"' added as prefix successfully." : "The '"+prefix+"' removed as prefix successfully.";
        e.getChannel().sendMessage(new EmbedBuilder()
                .setDescription(text)
                .setAuthor(e.getMessageAuthor().getName(), null, e.getMessageAuthor().getAvatar().getUrl().toString()));
    }
}