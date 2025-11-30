package de.kifo.common.enums.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionType {

    // COMMAND - Arguments
    USER_HAS_NO_DATA("No data could be found for this user."),
    SKIP_INDEX_NOT_FOUND("This index does not exist. Use \"/songlist\" to view the current song list."),
    ONLINE_TIME_WRONG_TIME_PERIOD("Please provide a valid value for the time period."),
    PAGE_NOT_AVAILABLE("This page does not exist in the current song list."),
    PLAYLIST_NOT_FOUND("The playlist could not be found."),
    PASSWORD_NOT_SECURE("The password must contain a number, uppercase and lowercase letters, a special character, and be at least 8 characters long."),

    // COMMAND - Miscellaneous
    NOT_IN_SPEECH_CHANNEL("You must be in a voice channel."),
    NO_SONG_RUNNING("No song is currently playing."),
    NO_SONG_IN_QUEUE("There is no song in the song list."),
    BOT_ALREADY_PLAYING_FOR_GUILD("The bot is already playing music in another channel."),
    USER_ALREADY_REGISTERED("This Discord account is already registered.");

    private final String message;
}
