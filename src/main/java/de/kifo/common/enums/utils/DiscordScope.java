package de.kifo.common.enums.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DiscordScope {

    GLOBAL("Global", ""),
    GUILD("Server", "auf diesem Server"),
    CHANNEL("Channel", "in diesem Channel");

    private final String displayName;
    private final String messageText;
}
