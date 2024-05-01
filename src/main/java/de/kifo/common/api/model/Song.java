package de.kifo.common.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Song {

    private Long id;
    private Long userId;
    private Long timesPlayed;
    private Long lastPlayDate;
    private String name;

}
