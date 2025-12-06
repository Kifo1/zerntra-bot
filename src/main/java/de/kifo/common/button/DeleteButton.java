package de.kifo.common.button;

import de.kifo.common.button.handle.ButtonBase;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

@ButtonBase.BotButton(label = "Delete", id = "123")
public class DeleteButton extends ButtonBase{

    public DeleteButton(@NotNull BotButton botButton) {
        super(botButton);
    }

    @Override
    public void onClick(ButtonInteractionEvent event) {
        event.deferEdit().queue(hook -> hook.deleteOriginal().queue());
    }
}
