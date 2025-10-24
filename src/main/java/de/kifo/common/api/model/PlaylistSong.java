package de.kifo.common.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaylistSong {

    private Long id; //TODO: Add owner User -> Check if user can be selected by playlist id via join system used
    private String name;
    private String uri;
    private Playlist playlist;
}
