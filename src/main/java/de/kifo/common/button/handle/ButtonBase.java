package de.kifo.common.button.handle;

import com.google.inject.Inject;
import de.kifo.JavaBot;
import lombok.Getter;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static net.dv8tion.jda.api.components.buttons.Button.secondary;

@Getter
public abstract class ButtonBase {

    @Inject
    private JavaBot javaBot;

    private final String label;
    private final String id;

    public ButtonBase(@NotNull BotButton botButton) {
        label = botButton.label();
        id = botButton.id();
    }

    public abstract void onClick(ButtonInteractionEvent event);

    public Button getButton() {
        return secondary(id, label);
    }

    public ActionRow getActionRow() {
        return ActionRow.of(getButton());
    }

    @Target(TYPE)
    @Retention(RUNTIME)
    public @interface BotButton {

        String label();

        String id();
    }
}
