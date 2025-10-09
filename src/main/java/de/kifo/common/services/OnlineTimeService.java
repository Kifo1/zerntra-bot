package de.kifo.common.services;

import de.kifo.JavaBot;
import de.kifo.common.api.model.VoiceChannelOnlineSession;
import de.kifo.common.enums.utils.DiscordScope;
import lombok.Data;

import java.util.HashMap;

import static java.lang.System.currentTimeMillis;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Data
public class OnlineTimeService {

    private final JavaBot javaBot;

    private HashMap<Long, VoiceChannelOnlineSession> voiceChannelOnlineSessions = new HashMap<>();

    public void startVoiceOnlineSession(Long userId, Long guildId, Long channelId) {
        voiceChannelOnlineSessions.putIfAbsent(userId,
                new VoiceChannelOnlineSession(null, guildId, channelId,
                        javaBot.getApi().getUserById(userId), currentTimeMillis(), 0L));
    }

    public VoiceChannelOnlineSession stopVoiceOnlineSession(Long userId) {
        VoiceChannelOnlineSession session = voiceChannelOnlineSessions.getOrDefault(userId, null);

        if (nonNull(session)) {
            session.setEndMillis(currentTimeMillis());
            javaBot.getApi().addVoiceChannelOnlineSessionToUser(userId, session);
            voiceChannelOnlineSessions.remove(userId);
        }

        return session;
    }

    public boolean isInAVoiceChannelSession(Long userId) {
        return nonNull(voiceChannelOnlineSessions.getOrDefault(userId, null));
    }

    public long getVoiceOnlineTime(Long userId, Long guildId, Long voiceChannelId, VoiceChannelOnlineSession.TimePeriod timePeriod, DiscordScope discordScope) {
        if (voiceChannelOnlineSessions.containsKey(userId)) {
            VoiceChannelOnlineSession session = stopVoiceOnlineSession(userId);
            startVoiceOnlineSession(userId, guildId, voiceChannelId == -1 ? session.getChannelId() : voiceChannelId);
        }

        return switch (discordScope) {
            case GUILD -> javaBot.getApi().getVoiceSessionSecondsForTimePeriodAndGuild(userId, timePeriod, guildId);
            case CHANNEL -> javaBot.getApi().getVoiceSessionSecondsForTimePeriodAndChannelInGuild(userId, timePeriod, guildId, voiceChannelId);
            default -> javaBot.getApi().getVoiceSessionSecondsForTimePeriod(userId, timePeriod);
        };
    }
}
