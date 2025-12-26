package de.kifo.common.enums.button;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ButtonType {

    DELETE("Delete", "d1"),
    STOP_MUSIC("⏹\uFE0F", "sm1"),
    TOGGLE_SONG("⏯", "tp1"),
    NEXT_SONG("➡\uFE0F", "ns1"),
    SHUFFLE_LIST("\uD83D\uDD00", "sl1");

    private final String label;
    private final String id;
}
