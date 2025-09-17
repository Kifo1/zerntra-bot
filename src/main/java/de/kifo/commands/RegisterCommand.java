package de.kifo.commands;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import de.kifo.JavaBot;
import de.kifo.commands.handle.CommandBase;
import de.kifo.common.api.model.User;
import de.kifo.common.exceptions.CommandException;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static de.kifo.common.enums.exception.ExceptionType.USER_ALREADY_REGISTERED;
import static de.kifo.common.enums.message.Message.MessageType.MESSAGE;
import static de.kifo.common.util.EmbedUtils.getEmbedMessageByText;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

@CommandBase.BotCommand(name = "register", description = "Registriere deinen Account.", hasOptions = true)
public class RegisterCommand extends CommandBase {

    @Inject
    private JavaBot javaBot;

    public RegisterCommand(@NotNull BotCommand command) {
        super(command);
    }

    @Override
    public void execute(Member member, TextChannel textChannel, List<OptionMapping> options, SlashCommandInteractionEvent event) throws CommandException {
        long userId = member.getIdLong();
        User user = javaBot.getApi().getUserById(userId);

        if (javaBot.getApi().isUserRegistered(user)) {
            throw new CommandException(USER_ALREADY_REGISTERED, event);
        }

        String password = options.get(0).getAsString();
        user.setPassword(password);
        javaBot.getApi().updateUser(user);

        event.replyEmbeds(
                getEmbedMessageByText("Dein Account wurde erfolgreich registriert.", MESSAGE)).
                setEphemeral(true)
                .queue();
    }

    @Override
    public void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event) {}

    @Override
    public List<OptionData> getOptions() {
        return ImmutableList.of(new OptionData(STRING, "passwort", "Wähle ein Passwort für deinen Account.", true, false));
    }
}
