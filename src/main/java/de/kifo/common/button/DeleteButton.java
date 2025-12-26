package de.kifo.common.button;

import de.kifo.common.button.handle.ButtonBase;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

import static de.kifo.common.enums.button.ButtonType.DELETE;

@ButtonBase.BotButton(buttonType = DELETE)
public class DeleteButton extends ButtonBase{

    public DeleteButton(@NotNull BotButton botButton) {
        super(botButton);
    }

    @Override
    public void onClick(ButtonInteractionEvent event) {
        event.deferEdit().queue(hook -> hook.deleteOriginal().queue());
    }
}
