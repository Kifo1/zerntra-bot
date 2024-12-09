package de.kifo.common.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class HistoryEntry {

    private Long id;
    private Long userId;
    private Type type;
    private ZonedDateTime dateTime;
    private String information;

    @Getter
    @AllArgsConstructor
    public enum Type {

        SONG_PLAY,
        COMMAND_USE,
        ONLINE_STATUS_CHANGE,
        CHANNEL_JOIN,
        CHANNEL_QUIT,
        CHANNEL_CHANGE;
    }
}
