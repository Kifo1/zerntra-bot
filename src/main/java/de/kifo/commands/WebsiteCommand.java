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

@CommandBase.BotCommand(name = "website", description = "Zeigt den Link zur Website an.")
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
                .addField("Website:", "Besuche http://91.99.27.95 um deine Statistiken zu sehen.", false)
                .setFooter("Nutze /register <Passwort>, um ein Passwort für deinen Account festzulegen.");

        event.replyEmbeds(builder.build()).queue();
    }

    @Override
    public void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event) {}
}
