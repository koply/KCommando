package me.koply.jdatest;

import me.koply.kcommando.integration.impl.jda.JDACommand;
import me.koply.kcommando.internal.annotations.Argument;
import me.koply.kcommando.internal.annotations.Commando;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Commando(name="Prefix Selector",
        aliases="prefix",
        guildOnly = true,
        onlyArguments = true)
public class PrefixCommand extends JDACommand {

    private final static Test testInstance = Test.getInstance();

    public PrefixCommand() {
        getInfo().setGuildOnlyCallback(e -> e.getMessage().addReaction("⛔").queue());
        getInfo().setOnFalseCallback(e -> e.getChannel().sendMessage("Usage: prefix <add|remove|list> <prefix>").queue());
    }

    @Override
    public boolean handle(MessageReceivedEvent e, String[] args) {
        return false;
    }

    @Argument(arg = "add")
    public boolean add(MessageReceivedEvent e, String[] args) {
        if (args.length < 3) return false;
        testInstance.addCustomPrefix(e.getGuild().getIdLong(), args[2]);
        sendInfo(e, args[2], true);
        return true;
    }

    @Argument(arg = "remove")
    public boolean remove(MessageReceivedEvent e, String[] args) {
        if (args.length < 3) return false;
        testInstance.removeCustomPrefix(e.getGuild().getIdLong(), args[2]);
        sendInfo(e, args[2], false);
        return true;
    }

    @Argument(arg = "list")
    public boolean list(MessageReceivedEvent e, String[] args) {
        if (testInstance.getCustomGuildPrefixes().containsKey(e.getGuild().getIdLong())) {
            StringBuilder sb = new StringBuilder();
            for (String s : testInstance.getCustomGuildPrefixes().get(e.getGuild().getIdLong())) {
                sb.append("`").append(s).append("` - ");
            }
            e.getChannel().sendMessage(new EmbedBuilder()
                    .setDescription("The prefixes are: " + sb).build()).queue();
        } else e.getChannel().sendMessage(new EmbedBuilder()
                .setDescription("This guild doesn't have any custom prefixes.").build()).queue();
        return true;
    }

    private void sendInfo(MessageReceivedEvent e, String prefix, boolean isAdded) {
        final String text = isAdded ? "The '"+prefix+"' added as prefix successfully." : "The '"+prefix+"' removed as prefix successfully.";
        e.getChannel().sendMessage(new EmbedBuilder()
                .setDescription(text)
                .setAuthor(e.getAuthor().getName(), null, e.getAuthor().getAvatarUrl())
                .build()).queue();
    }
}