package de.kifo.commands;

import com.google.inject.Inject;
import de.kifo.JavaBot;
import de.kifo.commands.handle.CommandBase;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@CommandBase.BotCommand(name = "invite", description = "Create an invite link.")
public class InviteCommand extends CommandBase {

    @Inject
    private JavaBot javaBot;

    public InviteCommand(@NotNull BotCommand command) {
        super(command);
    }

    @Override
    public void execute(Member member, TextChannel textChannel, List<OptionMapping> options, SlashCommandInteractionEvent event) {
        event.reply("Send this link to invite someone to the server:").queue();
        event.getMessageChannel().sendMessage(textChannel.createInvite().setMaxAge(3600).complete().getUrl()).queue();
        event.getMessageChannel().sendMessage("You can use the link for one hour.").queue();
    }

    @Override
    public void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event) {
        throw new UnsupportedOperationException("Invite command does not have arguments for auto complete.");
    }
}
