package de.kifo.listener;

import de.kifo.JavaBot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.List;

public class InviteCommandListener extends ListenerAdapter {

    @Inject
    private JavaBot javaBot;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        TextChannel textChannel = event.getChannel().asTextChannel();
        List<OptionMapping> options = event.getOptions();

        this.javaBot.getRegistry().getCommandBases().stream()
                .filter(command -> command.getName().equalsIgnoreCase(event.getCommandString().replaceAll("/", "")))
                .findFirst()
                .ifPresent(command -> command.execute(member, textChannel, options, event));
    }
}
