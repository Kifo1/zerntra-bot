package de.kifo.listener;

import com.google.inject.Inject;
import de.kifo.JavaBot;
import de.kifo.common.api.model.HistoryEntryDTO;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import static de.kifo.common.api.model.HistoryEntryDTO.Type.CHANNEL_CHANGE;
import static de.kifo.common.api.model.HistoryEntryDTO.Type.CHANNEL_JOIN;
import static de.kifo.common.api.model.HistoryEntryDTO.Type.CHANNEL_QUIT;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.nonNull;

public class GuildVoiceUpdateListener extends ListenerAdapter {

    @Inject
    private JavaBot javaBot;

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        User user = event.getMember().getUser();
        Long userId = user.getIdLong();
        Guild guild = event.getGuild();
        AudioChannelUnion channelJoined = event.getChannelJoined();
        AudioChannelUnion channelLeft = event.getChannelLeft();

        javaBot.getApi().createUser(userId, user.getName());

        HistoryEntryDTO.Type type;
        String information;
        if (nonNull(channelJoined) && nonNull(channelLeft)) {
            type = CHANNEL_CHANGE;
            information = channelLeft.getName() + " -> " + channelJoined.getName();
        } else {
            type = nonNull(channelJoined) ? CHANNEL_JOIN : CHANNEL_QUIT;
            information = nonNull(channelJoined) ? channelJoined.getName() : channelLeft.getName();
        }

        if (type == CHANNEL_JOIN) {
            javaBot.getApi().startVoiceSession(userId, guild.getIdLong(), channelJoined.asVoiceChannel().getIdLong());
        } else if (type == CHANNEL_QUIT) {
            javaBot.getApi().stopVoiceSession(userId);
        }

        javaBot.getApi().createHistoryEntry(new HistoryEntryDTO(null, guild.getIdLong(),
                userId, type, currentTimeMillis(), information));
    }
}
