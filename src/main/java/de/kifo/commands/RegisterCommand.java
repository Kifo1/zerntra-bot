package de.kifo.commands;

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
import java.util.regex.Pattern;

import static de.kifo.common.enums.exception.ExceptionType.PASSWORD_NOT_SECURE;
import static de.kifo.common.enums.message.Message.MessageType.MESSAGE;
import static de.kifo.common.util.EmbedUtils.getEmbedMessageByText;
import static java.util.regex.Pattern.compile;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

@CommandBase.BotCommand(name = "register", description = "Registriere deinen Account.", hasOptions = true)
public class RegisterCommand extends CommandBase {

    @Inject
    private JavaBot javaBot;

    private static final Pattern PASSWORD_PATTERN =
            compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");

    public RegisterCommand(@NotNull BotCommand command) {
        super(command);
    }

    @Override
    public void execute(Member member, TextChannel textChannel, List<OptionMapping> options, SlashCommandInteractionEvent event) throws CommandException {
        long userId = member.getIdLong();
        User user = javaBot.getApi().getUserById(userId);

        String password = options.get(0).getAsString();
        if (!isValidPassword(password)) {
            throw new CommandException(PASSWORD_NOT_SECURE, event);
        }
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
    public @NotNull List<OptionData> getOptions() {
        return List.of(new OptionData(STRING, "passwort", "Wähle ein Passwort für deinen Account.", true, false));
    }

    private boolean isValidPassword(@NotNull String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}
