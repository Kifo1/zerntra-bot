package de.kifo.listener;

import de.kifo.JavaBot;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class AutoCompleteListener extends ListenerAdapter {

    @Inject
    private JavaBot javaBot;

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        javaBot.getApi().createUser(event.getUser().getIdLong());
        String optionName = event.getFocusedOption().getName();

        this.javaBot.getRegistry().getCommandBases().stream()
                .filter(command -> command.getName().equalsIgnoreCase(event.getFullCommandName()))
                .findFirst()
                .ifPresent(command -> command.autoComplete(optionName, event));
    }
}
