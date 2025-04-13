package de.kifo.common.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;

@Data
@AllArgsConstructor
public class Playlist {

    private Long id;
    private User owner;
    private String name;
    private Boolean isPublic;
    private Collection<User> members;
    private Collection<User> editAllowedMembers;
    private Collection<PlaylistSong> playlistSongs;

}
