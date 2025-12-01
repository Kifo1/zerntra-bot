package de.kifo.common.button.handle;

import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.HashSet;
import java.util.Set;

@Getter
public abstract class Button {

    public static final Set<Button> registeredButtons = new HashSet<>();
    private final String label;
    private final ButtonClickFunction buttonClickFunction;

    protected Button(String label, ButtonClickFunction buttonClickFunction) {
        this.label = label;
        this.buttonClickFunction = buttonClickFunction;

        registeredButtons.add(this);
    }

    public interface ButtonClickFunction {
        void apply(ButtonInteractionEvent event);
    }

    public void delete() {
        registeredButtons.remove(this);
    }
}
