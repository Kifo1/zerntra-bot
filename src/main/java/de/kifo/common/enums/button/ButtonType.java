package de.kifo.common.enums.button;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ButtonType {

    DELETE("\uD83D\uDDD1", "d1"),
    STOP_MUSIC("⏹\uFE0F", "sm1"),
    TOGGLE_SONG("⏯", "tp1"),
    NEXT_SONG("➡\uFE0F", "ns1"),
    SHUFFLE_LIST("\uD83D\uDD00", "sl1"),
    PREVIOUS_PAGE("⬅\uFE0F", "pp1"),
    NEXT_PAGE("➡\uFE0F", "np1");

    private final String label;
    private final String id;
}
