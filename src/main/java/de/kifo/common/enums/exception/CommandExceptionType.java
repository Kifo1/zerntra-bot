package de.kifo.common.enums.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommandExceptionType {

    // Arguments
    USER_NOT_FOUND("Der Nutzer konnte nicht gefunden werden."),
    USER_HAS_NO_DATA("Es konnten keine Daten zu diesem User gefunden werden."),
    SONG_NOT_FOUND("Das Lied konnte nicht gefunden werden."),
    SKIP_INDEX_NOT_FOUND("Dieser Index existiert nicht. Verbende \"/songlist\", um die aktuelle Songlist anzuschauen."),
    PLAYLIST_NOT_FOUND("Die Playlist konnte nicht gefunden werden."),
    PLAYLIST_INDEX_NOT_FOUND("Dieser Index existiert nicht. Verwende \"/playlist info\", um die Playlist anzuschauen."),
    PLAYLIST_SONG_NOT_FOUND("Dieses Lied konnte in der angegebenen Playlist nicht gefunden werden."),
    PLAYLIST_ACCESS_MODIFIER_NEEDED("Bitte gib den Zugangs-Typen für die Playlist an."),

    // Miscellaneous
    NOT_IN_SPEECH_CHANNEL("Du musst in einem Sprachkanal sein."),
    NO_PERMISSION("Du hast nicht die nötige Berechtigung, um diese Aktion durchzuführen."),
    NO_SONG_RUNNING("Es läuft im Moment kein Lied."),
    PLAYLIST_ALREADY_EXISTS("Es existiert bereits eine Playlist die diesen Namen trägt."),
    USER_ALREADY_REGISTERED("Dieser Discord Account ist bereits registriert.");

    private final String message;

}
