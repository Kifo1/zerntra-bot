package de.kifo.commands.music;

import com.google.inject.Inject;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import de.kifo.JavaBot;
import de.kifo.commands.handle.CommandBase;
import de.kifo.common.exceptions.CommandException;
import de.kifo.common.music.GuildMusicManager;
import de.kifo.common.music.PlayerManager;
import de.kifo.common.music.TrackScheduler;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static de.kifo.common.enums.exception.ExceptionType.*;
import static de.kifo.common.enums.message.Message.MessageType.MESSAGE;
import static de.kifo.common.enums.message.Message.SONGLIST_SHUFFLED;
import static de.kifo.common.util.EmbedUtils.getEmbedMessageByText;
import static java.util.Objects.isNull;

@CommandBase.BotCommand(name = "shuffle", description = "Ändert die Reihenfolge der aktuellen songlist.")
public class ShuffleCommand extends CommandBase {

    @Inject
    private JavaBot javaBot;

    public ShuffleCommand(@NotNull BotCommand command) {
        super(command);
    }

    @Override
    public void execute(Member member, TextChannel textChannel, List<OptionMapping> options, SlashCommandInteractionEvent event) throws CommandException {
        GuildVoiceState guildVoiceState = member.getVoiceState();

        if (isNull(guildVoiceState) || isNull(guildVoiceState.getChannel())) {
            throw new CommandException(NOT_IN_SPEECH_CHANNEL, event);
        }

        PlayerManager playerManager = javaBot.getPlayerManager();
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(event.getGuild());
        TrackScheduler trackScheduler = guildMusicManager.getTrackScheduler();
        AudioPlayer audioPlayer = trackScheduler.getAudioPlayer();

        if (isNull(audioPlayer.getPlayingTrack())) {
            throw new CommandException(NO_SONG_RUNNING, event);
        } else if (trackScheduler.isQueueEmpty()) {
            throw new CommandException(NO_SONG_IN_QUEUE, event);
        }

        trackScheduler.shuffleQueue();
        event.replyEmbeds(getEmbedMessageByText(SONGLIST_SHUFFLED.getMessageText(), MESSAGE)).queue();
    }

    @Override
    public void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event) {}
}
