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

        WEEK("Week", 7),
        MONTH("Month", 30),
        YEAR("Year", 365),
        LIFETIME("Lifetime", MAX_VALUE);

        private final String displayName;
        private final int days;
    }
}
