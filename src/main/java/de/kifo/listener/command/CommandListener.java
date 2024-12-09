package de.kifo.listener.command;

import de.kifo.JavaBot;
import de.kifo.common.api.model.HistoryEntry;
import de.kifo.common.api.model.User;
import de.kifo.common.exceptions.CommandException;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.List;

import static de.kifo.common.api.model.HistoryEntry.Type.COMMAND_USE;
import static java.lang.System.currentTimeMillis;

public class CommandListener extends ListenerAdapter {

    @Inject
    private JavaBot javaBot;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        javaBot.getApi().updateUserOrCreate(new User(event.getUser().getIdLong(), event.getUser().getName(), null, currentTimeMillis()));
        Member member = event.getMember();
        TextChannel textChannel = event.getChannel().asTextChannel();
        List<OptionMapping> options = event.getOptions();

        this.javaBot.getRegistry().getCommandBases().stream()
                .filter(command -> command.getName().equalsIgnoreCase(event.getFullCommandName()))
                .findFirst()
                .ifPresent(command -> {
                    try {
                        command.execute(member, textChannel, options, event);
                        javaBot.getApi().createHistoryEntry(new HistoryEntry(0L, member.getIdLong(), COMMAND_USE, currentTimeMillis(),
                                command.getName() + " in channel " + textChannel.getName()));
                    } catch (CommandException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
