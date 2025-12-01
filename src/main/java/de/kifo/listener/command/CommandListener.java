package de.kifo.listener.command;

import com.google.inject.Inject;
import de.kifo.JavaBot;
import de.kifo.common.api.model.HistoryEntryDTO;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static de.kifo.common.api.model.HistoryEntryDTO.Type.COMMAND_USE;
import static java.lang.System.currentTimeMillis;

public class CommandListener extends ListenerAdapter {

    @Inject
    private JavaBot javaBot;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        User user = event.getUser();
        javaBot.getApi().createUser(user.getIdLong(), user.getName());
        Member member = event.getMember();
        TextChannel textChannel = event.getChannel().asTextChannel();
        Guild guild = event.getGuild();
        List<OptionMapping> options = event.getOptions();

        this.javaBot.getRegistry().getCommandBases().stream()
                .filter(command -> command.getName().equalsIgnoreCase(event.getFullCommandName()))
                .findFirst()
                .ifPresent(command -> {
                    command.executeInitialization(member, textChannel, options, event);
                    javaBot.getApi().createHistoryEntry(new HistoryEntryDTO(null, guild.getIdLong(), member.getIdLong(),
                            COMMAND_USE, currentTimeMillis(), command.getName() + " in channel " + textChannel.getName()));
                });
    }
}
