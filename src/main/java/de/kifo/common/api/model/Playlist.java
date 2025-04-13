package de.kifo.common.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;

@Data
@AllArgsConstructor
public class Playlist {

    private Long id;
    private Long userId;
    private String name;
    private Boolean publicAccess;
    private Collection<String> songs;

}
