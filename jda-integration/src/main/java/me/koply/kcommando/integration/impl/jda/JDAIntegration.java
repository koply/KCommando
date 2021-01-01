package me.koply.kcommando.integration.impl.jda;

import me.koply.kcommando.CommandHandler;
import me.koply.kcommando.integration.Integration;
import net.dv8tion.jda.api.JDA;

public class JDAIntegration extends Integration {

    private final JDA jda;

    public JDAIntegration(final JDA jda) {
        super(jda.getSelfUser().getIdLong());
        this.jda = jda;
    }

    @Override
    public void register(CommandHandler commandHandler) {
        jda.addEventListener(new JDAMessageListener(commandHandler));
    }

}