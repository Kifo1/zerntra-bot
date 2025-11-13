package de.kifo.common.enums.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionType {

    // COMMAND - Arguments
    USER_HAS_NO_DATA("Es konnten keine Daten zu diesem User gefunden werden."),
    SKIP_INDEX_NOT_FOUND("Dieser Index existiert nicht. Verbende \"/songlist\", um die aktuelle Songlist anzuschauen."),
    ONLINE_TIME_WRONG_TIME_PERIOD("Bitte gib einen gültigen Wert für den Zeitraum an."),
    PAGE_NOT_AVAILABLE("Die aktuelle Songlist besitzt diese Seite nicht."),

    // COMMAND - Miscellaneous
    NOT_IN_SPEECH_CHANNEL("Du musst in einem Sprachkanal sein."),
    NO_SONG_RUNNING("Es läuft im Moment kein Lied."),
    NO_SONG_IN_QUEUE("Es ist kein Lied in der Songlist."),
    BOT_ALREADY_PLAYING_FOR_GUILD("Der Bot spielt bereits in einem anderen Channel Musik ab."),
    USER_ALREADY_REGISTERED("Dieser Discord Account ist bereits registriert.");

    private final String message;
}
