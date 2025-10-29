package de.kifo.common.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SongDTO {

    private Long id;
    private Long userId;
    private Long timesPlayed;
    private Long lastPlayDate;
    private String name;
    private String uri;

}
