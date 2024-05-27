package de.kifo.listener;

import de.kifo.JavaBot;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.inject.Inject;

public class ReadyListener extends ListenerAdapter {

    @Inject
    private JavaBot javaBot;

    @Override
    public void onReady(ReadyEvent event) {
        javaBot.getJda().updateCommands().addCommands(javaBot.getRegistry().getCommandBases()).queue();
    }
}
