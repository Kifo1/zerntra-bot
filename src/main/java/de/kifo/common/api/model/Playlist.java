package de.kifo.common.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;

@Data
@AllArgsConstructor
public class Playlist {

    Long id;
    Long userId;
    String name;
    Boolean publicAccess;
    Collection<String> songs;

}
