package de.kifo.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PlaylistAction {

    CREATE_PLAYLIST("Create", false),
    DELETE_PLAYLIST("Delete", false),
    PLAY("Play", false),
    INFO("Info", false),
    ADD_SONG("Add", true),
    REMOVE_SONG("Remove", true);

    private final String actionName;
    private final boolean modificationAction;

}
