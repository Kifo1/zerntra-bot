package de.kifo.commands;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import de.kifo.JavaBot;
import de.kifo.commands.handle.CommandBase;
import de.kifo.common.api.model.VoiceChannelOnlineSession;
import de.kifo.common.exceptions.CommandException;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static de.kifo.JavaBot.onlineTimeService;
import static de.kifo.common.api.model.VoiceChannelOnlineSession.TimePeriod.LIFETIME;
import static de.kifo.common.enums.exception.ExceptionType.ONLINE_TIME_WRONG_TIME_PERIOD;
import static de.kifo.common.enums.message.Message.MessageType.MESSAGE;
import static de.kifo.common.util.EmbedUtils.getEmbedMessageByText;
import static de.kifo.common.util.StringUtils.getTimeStringBySeconds;
import static java.util.Arrays.stream;
import static java.util.Objects.isNull;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

@CommandBase.BotCommand(name = "onlinetime", description = "Zeigt an, wie viel Zeit du in einem bestimmten Zeitraum im Rasselbande Discord verbracht hast.", hasOptions = true)
public class OnlineTimeCommand extends CommandBase {

    @Inject
    private JavaBot javaBot;

    public OnlineTimeCommand(@NotNull BotCommand command) {
        super(command);
    }

    @Override
    public void execute(Member member, TextChannel textChannel, List<OptionMapping> options, SlashCommandInteractionEvent event) throws CommandException {
        long userId = member.getUser().getIdLong();
        VoiceChannelOnlineSession.TimePeriod timePeriod = stream(VoiceChannelOnlineSession.TimePeriod.values())
                .filter(period -> period.getDisplayName().equalsIgnoreCase(options.get(0).getAsString()))
                .findFirst().orElse(null);

        if (isNull(timePeriod)) {
            throw new CommandException(ONLINE_TIME_WRONG_TIME_PERIOD, event);
        }

        long onlineSeconds = onlineTimeService.getVoiceOnlineTime(userId, timePeriod);
        String timeString = getTimeStringBySeconds(onlineSeconds);

        event.replyEmbeds(getEmbedMessageByText(
                "Du warst " + (timePeriod == LIFETIME ? "insgesamt " : "in den letzten " + timePeriod.getDays() + " Tagen ") +
                        timeString + " auf dem Rasselbande Discord online.", MESSAGE)).queue();
    }

    @Override
    public void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event) {
        List<Command.Choice> choices = stream(VoiceChannelOnlineSession.TimePeriod.values())
                .map(VoiceChannelOnlineSession.TimePeriod::getDisplayName)
                .filter(timePeriodName -> timePeriodName.toLowerCase().startsWith(event.getFocusedOption().getValue().toLowerCase()))
                .map(timePeriodName -> new Command.Choice(timePeriodName, timePeriodName))
                .toList();

        event.replyChoices(choices).queue();
    }

    @Override
    public List<OptionData> getOptions() {
        return ImmutableList.of(new OptionData(STRING, "zeitraum", "Der Zeitraum, für den du deine Online Zeit wissen willst", true, true));
    }
}
