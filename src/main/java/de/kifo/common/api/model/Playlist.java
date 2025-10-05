package de.kifo.common.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Playlist {

    private Long id;
    private User owner;
    private String name;
    private Boolean isPublic;
    private List<User> members;
    private List<User> editAllowedMembers;
    private List<PlaylistSong> playlistSongs;

}
