package de.kifo.common.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VoiceChannelOnlineSession {

    private Long id;
    private Long userId;
    private Long startMillis;
    private Long endMillis;

}
