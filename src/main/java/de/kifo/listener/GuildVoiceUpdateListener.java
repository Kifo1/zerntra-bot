package de.kifo.listener;

import de.kifo.JavaBot;
import de.kifo.common.api.model.User;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import static java.lang.System.currentTimeMillis;

public class GuildVoiceUpdateListener extends ListenerAdapter {

    @Inject
    private JavaBot javaBot;

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        javaBot.getApi().updateUser(new User(event.getMember().getUser().getIdLong(), event.getMember().getUser().getName(), currentTimeMillis()));
    }
}
