package de.kifo.common.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaylistMemberDTO {

    private Long id;
    private UserDTO user;
    private PlaylistMemberDTO.Role role;

    public enum Role {

        VIEWER,
        EDITOR;
    }
}
