package de.kifo.listener.button;

import com.google.inject.Inject;
import de.kifo.JavaBot;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ButtonClickListener extends ListenerAdapter {

    @Inject
    private JavaBot javaBot;

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        User user = event.getUser();
        javaBot.getApi().createUser(user.getIdLong(), user.getName());

        javaBot.getRegistry().getButtonBases().values().stream()
                .filter(button -> button.getId().equals(event.getButton().getCustomId()))
                .findFirst()
                .ifPresent(button -> button.onClick(event));
    }
}
