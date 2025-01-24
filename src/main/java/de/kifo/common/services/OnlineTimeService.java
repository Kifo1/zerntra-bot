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

    public void startVoiceOnlineSession(User user) {
        voiceChannelOnlineSessions.putIfAbsent(user.getId(),
                new VoiceChannelOnlineSession(0L, user.getId(), currentTimeMillis(), 0L));
    }

    public void stopVoiceOnlineSession(User user) {
        VoiceChannelOnlineSession session = voiceChannelOnlineSessions.getOrDefault(user.getId(), null);

        if (nonNull(session)) {
            //TODO Save in API
        }
    }

    public int getVoiceOnlineTime(User user) {
        if (nonNull(voiceChannelOnlineSessions.get(user.getId()))) {
            stopVoiceOnlineSession(user);
        }

        //TODO Receive online time from api

        startVoiceOnlineSession(user);

        return 0;
    }
}
