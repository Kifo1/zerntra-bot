package de.kifo.common.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import static java.lang.Integer.MAX_VALUE;

@Data
@AllArgsConstructor
public class VoiceChannelOnlineSession {

    private Long id;
    private User user;
    private Long startMillis;
    private Long endMillis;

    @Getter
    @AllArgsConstructor
    public enum TimePeriod {

        WEEK(7),
        MONTH(30),
        YEAR(365),
        LIFETIME(MAX_VALUE);

        private final int days;
    }
}
