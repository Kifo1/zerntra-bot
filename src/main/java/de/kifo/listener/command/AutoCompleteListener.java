package de.kifo.listener.command;

import de.kifo.JavaBot;
import de.kifo.common.api.model.User;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import static java.lang.System.currentTimeMillis;

public class AutoCompleteListener extends ListenerAdapter {

    @Inject
    private JavaBot javaBot;

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        javaBot.getApi().updateUser(new User(event.getUser().getIdLong(), event.getUser().getName(), currentTimeMillis()));
        String optionName = event.getFocusedOption().getName();

        this.javaBot.getRegistry().getCommandBases().stream()
                .filter(command -> command.getName().equalsIgnoreCase(event.getFullCommandName()))
                .findFirst()
                .ifPresent(command -> command.autoComplete(optionName, event));
    }
}
