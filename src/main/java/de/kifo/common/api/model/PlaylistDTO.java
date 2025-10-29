package de.kifo.common.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PlaylistDTO {

    private Long id;
    private UserDTO owner;
    private String name;
    private Boolean isPublic;
    private List<PlaylistSongDTO> playlistSongs;
    private List<PlaylistMemberDTO> playlistMember;
    private UserDTO lastEditUser;
    private Long lastEditTime;
    private Long createdAt;
}
