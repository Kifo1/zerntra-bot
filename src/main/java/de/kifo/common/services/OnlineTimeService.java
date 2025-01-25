package de.kifo.common.services;

import de.kifo.JavaBot;
import de.kifo.common.api.model.User;
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
                new VoiceChannelOnlineSession(0L, javaBot.getApi().getUserById(userId), currentTimeMillis(), 0L));
    }

    public void stopVoiceOnlineSession(Long userId) {
        VoiceChannelOnlineSession session = voiceChannelOnlineSessions.getOrDefault(userId, null);

        if (nonNull(session)) {
            User user = javaBot.getApi().getUserById(userId);

            session.setEndMillis(currentTimeMillis());
            user.addVoiceChannelSession(session);

            javaBot.getApi().updateUser(user);
        }
    }

    public boolean isInAVoiceChannelSession(Long userId) {
        return nonNull(voiceChannelOnlineSessions.getOrDefault(userId, null));
    }

    public int getVoiceOnlineTime(Long userId) {
        if (nonNull(voiceChannelOnlineSessions.get(userId))) {
            stopVoiceOnlineSession(userId);
        }

        //TODO Receive online time from api

        startVoiceOnlineSession(userId);

        return 0;
    }
}
