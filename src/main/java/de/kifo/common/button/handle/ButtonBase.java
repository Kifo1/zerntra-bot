package de.kifo.common.button.handle;

import com.google.inject.Inject;
import de.kifo.JavaBot;
import de.kifo.common.enums.button.ButtonType;
import lombok.Getter;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static net.dv8tion.jda.api.components.buttons.Button.secondary;

@Getter
public abstract class ButtonBase {

    @Inject
    public JavaBot javaBot;

    private final ButtonType buttonType;

    public ButtonBase(BotButton botButton) {
        this.buttonType = botButton.buttonType();
    }

    public abstract void onClick(ButtonInteractionEvent event);

    public Button getButton() {
        return secondary(buttonType.getId(), buttonType.getLabel());
    }

    public ActionRow getActionRow() {
        return ActionRow.of(getButton());
    }

    @Target(TYPE)
    @Retention(RUNTIME)
    public @interface BotButton {

        ButtonType buttonType();
    }
}
