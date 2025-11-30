package de.kifo.common.enums.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DiscordScope {

    GLOBAL("Global", ""),
    GUILD("Server", "on this server"),
    CHANNEL("Channel", "in this channel");

    private final String displayName;
    private final String messageText;
}
