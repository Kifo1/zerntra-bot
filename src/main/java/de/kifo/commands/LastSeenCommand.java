package de.kifo.commands;

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

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

import static de.kifo.common.enums.exception.ExceptionType.USER_HAS_NO_DATA;
import static de.kifo.common.enums.message.Message.MessageType.MESSAGE;
import static java.time.Instant.ofEpochMilli;
import static java.time.LocalDateTime.now;
import static java.time.ZoneId.systemDefault;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.List.of;
import static java.util.Objects.isNull;
import static net.dv8tion.jda.api.interactions.commands.OptionType.USER;

@CommandBase.BotCommand(name = "lastseen", description = "Schaue nach, wann jemand zuletzt online war.", hasOptions = true)
public class LastSeenCommand extends CommandBase {

    @Inject
    private JavaBot javaBot;

    public LastSeenCommand(@NotNull BotCommand command) {
        super(command);
    }

    @Override
    public void execute(Member member, TextChannel textChannel, List<OptionMapping> options, SlashCommandInteractionEvent event) throws CommandException {
        long requestedUserId = options.get(0).getAsUser().getIdLong();
        User user = javaBot.getApi().getUserById(requestedUserId);

        if (isNull(user.getLastOnTime())) {
            throw new CommandException(USER_HAS_NO_DATA, event, javaBot);
        }

        long lastOnTime = user.getLastOnTime();
        LocalDateTime lastSeenDate = ofEpochMilli(lastOnTime).atZone(systemDefault()).toLocalDateTime();
        long pastDays = DAYS.between(lastSeenDate, now());

        event.replyEmbeds(javaBot.getEmbedUtils().getEmbedMessageByText(
                user.getUserName() + " war zuletzt am " + lastSeenDate.format(ofPattern("dd.MM.yyyy")) + " um " + lastSeenDate.format(ofPattern("HH:mm:ss")) + " Uhr online. Das ist " + pastDays + " Tage her.", MESSAGE)).queue();
    }

    @Override
    public void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event) {}

    @Override
    public List<OptionData> getOptions() {
        return of(new OptionData(USER, "user", "User, von dem du wissen willst, wann er zuletzt online war.", true, false));
    }
}
