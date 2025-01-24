package de.kifo.listener;

import com.google.inject.Inject;
import de.kifo.JavaBot;
import de.kifo.common.api.model.HistoryEntry;
import de.kifo.common.api.model.User;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import static de.kifo.common.api.model.HistoryEntry.Type.CHANNEL_CHANGE;
import static de.kifo.common.api.model.HistoryEntry.Type.CHANNEL_JOIN;
import static de.kifo.common.api.model.HistoryEntry.Type.CHANNEL_QUIT;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.nonNull;

public class GuildVoiceUpdateListener extends ListenerAdapter {

    @Inject
    private JavaBot javaBot;

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        Long userId = event.getMember().getIdLong();

        javaBot.getApi().updateUserOrCreate(new User(userId, event.getMember().getUser().getName(), null, currentTimeMillis()));

        HistoryEntry.Type type;
        String information;
        if (nonNull(event.getChannelJoined()) && nonNull(event.getChannelLeft())) {
            type = CHANNEL_CHANGE;
            information = event.getChannelLeft().getName() + " -> " + event.getChannelJoined().getName();
        } else {
            type = nonNull(event.getChannelJoined()) ? CHANNEL_JOIN : CHANNEL_QUIT;
            information = nonNull(event.getChannelJoined()) ? event.getChannelJoined().getName() : event.getChannelLeft().getName();
        }
        javaBot.getApi().createHistoryEntry(new HistoryEntry(0L, userId, type, currentTimeMillis(), information));
    }
}
