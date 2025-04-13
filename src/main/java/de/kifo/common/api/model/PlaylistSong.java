package de.kifo.common.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaylistSong {

    private Long id;
    private String name;
    private String url;
}
