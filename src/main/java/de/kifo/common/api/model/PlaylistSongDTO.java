package de.kifo.common.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaylistSongDTO {

    private Long id;
    private Long playlistId;
    private String name;
    private String uri;
    private UserDTO updatedBy;
    private Long updatedAt;
    private UserDTO createdBy;
    private Long createdAt;
}
