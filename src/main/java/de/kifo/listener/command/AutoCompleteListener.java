package de.kifo.listener.command;

import com.google.inject.Inject;
import de.kifo.JavaBot;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class AutoCompleteListener extends ListenerAdapter {

    @Inject
    private JavaBot javaBot;

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        User user = event.getUser();
        javaBot.getApi().createUser(user.getIdLong(), user.getName());
        String optionName = event.getFocusedOption().getName();

        this.javaBot.getRegistry().getCommandBases().stream()
                .filter(command -> command.getName().equalsIgnoreCase(event.getFullCommandName()))
                .findFirst()
                .ifPresent(command -> command.autoComplete(optionName, event));
    }
}
