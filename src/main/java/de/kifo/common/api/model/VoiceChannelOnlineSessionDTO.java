package de.kifo.common.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import static java.lang.Integer.MAX_VALUE;

@Data
@AllArgsConstructor
public class VoiceChannelOnlineSessionDTO {

    private Long id;
    private Long guildId;
    private Long channelId;
    private UserDTO user;
    private Long startMillis;
    private Long endMillis;

    @Getter
    @AllArgsConstructor
    public enum TimePeriod {

        WEEK("Woche", 7),
        MONTH("Monat", 30),
        YEAR("Jahr", 365),
        LIFETIME("Insgesamt", MAX_VALUE);

        private final String displayName;
        private final int days;
    }
}
