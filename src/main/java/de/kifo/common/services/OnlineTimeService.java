package de.kifo.common.services;

import de.kifo.JavaBot;
import de.kifo.common.api.model.VoiceChannelOnlineSession;
import lombok.Data;

import java.util.HashMap;

import static java.lang.System.currentTimeMillis;
import static java.util.Objects.nonNull;

@Data
public class OnlineTimeService {

    private final JavaBot javaBot;

    private HashMap<Long, VoiceChannelOnlineSession> voiceChannelOnlineSessions = new HashMap<>();

    public void startVoiceOnlineSession(Long userId) {
        voiceChannelOnlineSessions.putIfAbsent(userId,
                new VoiceChannelOnlineSession(null, javaBot.getApi().getUserById(userId), currentTimeMillis(), 0L));
    }

    public void stopVoiceOnlineSession(Long userId) {
        VoiceChannelOnlineSession session = voiceChannelOnlineSessions.getOrDefault(userId, null);

        if (nonNull(session)) {
            session.setEndMillis(currentTimeMillis());
            javaBot.getApi().addVoiceChannelOnlineSessionToUser(userId, session);
            voiceChannelOnlineSessions.remove(userId);
        }
    }

    public boolean isInAVoiceChannelSession(Long userId) {
        return nonNull(voiceChannelOnlineSessions.getOrDefault(userId, null));
    }

    public long getVoiceOnlineTime(Long userId, VoiceChannelOnlineSession.TimePeriod timePeriod) {
        if (voiceChannelOnlineSessions.containsKey(userId)) {
            stopVoiceOnlineSession(userId);
            startVoiceOnlineSession(userId);
        }

        return javaBot.getApi().getVoiceSessionSecondsForTimePeriod(userId, timePeriod);
    }
}
