package de.kifo.commands;

import com.google.inject.Inject;
import de.kifo.JavaBot;
import de.kifo.commands.handle.CommandBase;
import de.kifo.common.api.model.UserDTO;
import de.kifo.common.exceptions.CommandException;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;

import static de.kifo.JavaBot.messageService;
import static de.kifo.JavaBot.onlineTimeService;
import static de.kifo.common.enums.exception.ExceptionType.USER_HAS_NO_DATA;
import static java.time.Instant.ofEpochMilli;
import static java.time.LocalDateTime.now;
import static java.time.ZoneId.systemDefault;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Objects.isNull;
import static net.dv8tion.jda.api.interactions.commands.OptionType.USER;

@CommandBase.BotCommand(name = "lastseen", description = "Check when someone was last online.", hasOptions = true)
public class LastSeenCommand extends CommandBase {

    @Inject
    private JavaBot javaBot;

    public LastSeenCommand(@NotNull BotCommand command) {
        super(command);
    }

    @Override
    public void execute(Member member, TextChannel textChannel, List<OptionMapping> options, SlashCommandInteractionEvent event) throws CommandException {
        long requestedUserId = options.get(0).getAsUser().getIdLong();
        UserDTO userDTO = javaBot.getApi().getUserById(requestedUserId);

        if (isNull(userDTO.getLastOnTime())) {
            throw new CommandException(USER_HAS_NO_DATA, event);
        }

        long lastOnTime = userDTO.getLastOnTime();
        LocalDateTime lastSeenDate = ofEpochMilli(lastOnTime).atZone(systemDefault()).toLocalDateTime();
        long pastDays = DAYS.between(lastSeenDate, now());

        if (!onlineTimeService.isInAVoiceChannelSession(userDTO.getId())) {
            event.replyEmbeds(messageService.message(
                    userDTO.getUserName() + "was last online on " + lastSeenDate.format(ofPattern("dd.MM.yyyy")) + " at " + lastSeenDate.format(ofPattern("HH:mm:ss")) + ". That was " + pastDays + " days ago.")).queue();
        } else {
            event.replyEmbeds(messageService.message(
                    userDTO.getUserName() + " is currently online in a voice channel.")).queue();
        }
    }

    @Override
    public void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event) {}

    @Override
    public @NotNull List<OptionData> getOptions() {
        return List.of(new OptionData(USER, "user", "The user whose last online time you want to check", true, false));
    }
}
