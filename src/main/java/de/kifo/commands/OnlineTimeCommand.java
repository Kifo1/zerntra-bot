package de.kifo.commands;

import com.google.inject.Inject;
import de.kifo.JavaBot;
import de.kifo.commands.handle.CommandBase;
import de.kifo.common.api.model.VoiceChannelOnlineSessionDTO;
import de.kifo.common.enums.utils.DiscordScope;
import de.kifo.common.exceptions.CommandException;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static de.kifo.JavaBot.messageService;
import static de.kifo.JavaBot.onlineTimeService;
import static de.kifo.common.api.model.VoiceChannelOnlineSessionDTO.TimePeriod.LIFETIME;
import static de.kifo.common.enums.exception.ExceptionType.NOT_IN_SPEECH_CHANNEL;
import static de.kifo.common.enums.exception.ExceptionType.ONLINE_TIME_WRONG_TIME_PERIOD;
import static de.kifo.common.enums.utils.DiscordScope.CHANNEL;
import static de.kifo.common.enums.utils.DiscordScope.GLOBAL;
import static de.kifo.common.util.StringUtils.getTimeStringBySeconds;
import static java.util.Arrays.stream;
import static java.util.Objects.isNull;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

@CommandBase.BotCommand(name = "onlinetime", description = "Shows how much time you have spent in Discord voice channels over a given period.", hasOptions = true)
public class OnlineTimeCommand extends CommandBase {

    @Inject
    private JavaBot javaBot;

    public OnlineTimeCommand(@NotNull BotCommand command) {
        super(command);
    }

    @Override
    public void execute(Member member, TextChannel textChannel, List<OptionMapping> options, SlashCommandInteractionEvent event) throws CommandException {
        long userId = member.getUser().getIdLong();
        GuildVoiceState guildVoiceState = member.getVoiceState();
        VoiceChannelOnlineSessionDTO.TimePeriod timePeriod = stream(VoiceChannelOnlineSessionDTO.TimePeriod.values())
                .filter(period -> period.getDisplayName().equalsIgnoreCase(options.get(0).getAsString()))
                .findFirst().orElse(null);
        DiscordScope discordScope = stream(DiscordScope.values())
                .filter(scope -> options.size() > 1 &&
                        scope.getDisplayName().equalsIgnoreCase(options.get(1).getAsString()))
                .findFirst().orElse(GLOBAL);

        if (isNull(timePeriod)) {
            throw new CommandException(ONLINE_TIME_WRONG_TIME_PERIOD, event);
        }

        long voiceChannelId = -1;
        if (discordScope == CHANNEL) {
            if (isNull(guildVoiceState) || isNull(guildVoiceState.getChannel())) {
                throw new CommandException(NOT_IN_SPEECH_CHANNEL, event);
            }
            voiceChannelId = guildVoiceState.getChannel().asVoiceChannel().getIdLong();
        }

        long onlineSeconds = onlineTimeService.getVoiceOnlineTime(userId, event.getGuild().getIdLong(), voiceChannelId, timePeriod, discordScope);
        String timeString = getTimeStringBySeconds(onlineSeconds);

        String lastOnlineMessage = "You have been online in voice channels " +
                (timePeriod == LIFETIME ? "for a total of "
                        : "over the last " + timePeriod.getDays() + " days for ") +
                timeString + " " + discordScope.getMessageText() + ".";

        event.replyEmbeds(messageService.message(lastOnlineMessage)).queue();
    }

    @Override
    public void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event) {
        List<Command.Choice> choices = List.of();

        if (optionName.equalsIgnoreCase("period")) {
            choices = stream(VoiceChannelOnlineSessionDTO.TimePeriod.values())
                    .map(VoiceChannelOnlineSessionDTO.TimePeriod::getDisplayName)
                    .filter(timePeriodName -> timePeriodName.toLowerCase().startsWith(event.getFocusedOption().getValue().toLowerCase()))
                    .map(timePeriodName -> new Command.Choice(timePeriodName, timePeriodName))
                    .toList();
        } else if (optionName.equalsIgnoreCase("area")) {
            choices = stream(DiscordScope.values())
                    .map(DiscordScope::getDisplayName)
                    .filter(scopeName -> scopeName.toLowerCase().startsWith(event.getFocusedOption().getValue().toLowerCase()))
                    .map(scopeName -> new Command.Choice(scopeName, scopeName))
                    .toList();
        }

        event.replyChoices(choices).queue();
    }

    @Override
    public @NotNull List<OptionData> getOptions() {
        return List.of(
                new OptionData(STRING, "period", "The period for which you want to check your online time", true, true),
                new OptionData(STRING, "area", "The Discord area where you were online", false, true));
    }
}
