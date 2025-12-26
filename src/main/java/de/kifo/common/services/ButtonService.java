package de.kifo.common.services;

import de.kifo.JavaBot;
import de.kifo.common.button.handle.ButtonBase;
import lombok.Data;
import net.dv8tion.jda.api.components.actionrow.ActionRow;

import static de.kifo.common.button.handle.ButtonBase.ButtonType.*;

@Data
public class ButtonService {

    private final JavaBot javaBot;

    public ActionRow getSongActionRow() {
        return ActionRow.of(
                getButtonByType(TOGGLE_SONG).getButton(),
                getButtonByType(NEXT_SONG).getButton(),
                getButtonByType(SHUFFLE_LIST).getButton());
    }

    public ButtonBase getButtonByType(ButtonBase.ButtonType buttonType) {
        return javaBot.getRegistry().getButtonBases().get(buttonType);
    }
}
