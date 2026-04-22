package de.kifo.commands;

import com.google.inject.Inject;
import de.kifo.JavaBot;
import de.kifo.commands.handle.CommandBase;
import de.kifo.common.exceptions.CommandException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static java.awt.Color.MAGENTA;

@CommandBase.BotCommand(name = "website", description = "Displays the link to the website.")
public class WebsiteCommand extends CommandBase {

    @Inject
    private JavaBot javaBot;

    public WebsiteCommand(@NotNull BotCommand command) {
        super(command);
    }

    @Override
    public void execute(Member member, TextChannel textChannel, List<OptionMapping> options, SlashCommandInteractionEvent event) throws CommandException {
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(MAGENTA)
                .addField("Website:", "Visit https://zerntra.org to view your statistics.", false)
                .setFooter("Use /register <password> to set a password for your account.");

        event.replyEmbeds(builder.build()).queue();
    }

    @Override
    public void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event) {
        throw new UnsupportedOperationException("Website command does not have arguments for auto complete.");
    }
}
