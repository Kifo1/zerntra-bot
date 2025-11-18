package de.kifo.common.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PlaylistDTO {

    private Long id;
    private UserDTO createdBy;
    private String name;
    private Boolean isPublic;
    private List<PlaylistSongDTO> songs;
    private List<PlaylistMemberDTO> members;
    private UserDTO updatedBy;
    private Long updatedAt;
    private Long createdAt;
}
