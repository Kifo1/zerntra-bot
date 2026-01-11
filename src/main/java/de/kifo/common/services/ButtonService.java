package de.kifo.common.services;

import de.kifo.JavaBot;
import de.kifo.common.button.handle.ButtonBase;
import de.kifo.common.enums.button.ButtonType;
import lombok.Data;
import net.dv8tion.jda.api.components.actionrow.ActionRow;

import static de.kifo.common.enums.button.ButtonType.*;

@Data
public class ButtonService {

    private final JavaBot javaBot;

    public ActionRow getSongActionRow(boolean isPlaying) {
        return ActionRow.of(
                getButtonByType(STOP_MUSIC).getButton(),
                getButtonByType(isPlaying ? TOGGLE_PLAYING_TO_OFF : TOGGLE_PLAYING_TO_ON ).getButton(),
                getButtonByType(NEXT_SONG).getButton(),
                getButtonByType(SHUFFLE_LIST).getButton());
    }

    public ButtonBase getButtonByType(ButtonType buttonType) {
        return javaBot.getRegistry().getButtonBases().get(buttonType);
    }
}
