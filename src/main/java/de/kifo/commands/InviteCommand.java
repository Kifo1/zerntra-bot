package de.kifo.commands;

import de.kifo.JavaBot;
import de.kifo.commands.handle.CommandBase;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.List;

@CommandBase.Command(name = "invite", description = "create an invite link")
public class InviteCommand extends CommandBase {

    @Inject
    private JavaBot javaBot;

    public InviteCommand(@NotNull CommandBase.Command command) {
        super(command);
    }

    @Override
    public void execute(Member member, TextChannel textChannel, List<OptionMapping> options, SlashCommandInteractionEvent event) {
        event.reply("Verschicke diesen Link, um jemanden zum Server einzuladen:").queue();
        event.getMessageChannel().sendMessage(textChannel.createInvite().setMaxAge(3600).complete().getUrl()).queue();
        event.getMessageChannel().sendMessage("Du kannst den Link für eine Stunde nutzen.").queue();
    }

    @Override
    public void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event) {}
}
