package de.kifo.commands.music;

import com.google.inject.Inject;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import de.kifo.JavaBot;
import de.kifo.commands.handle.CommandBase;
import de.kifo.common.exceptions.CommandException;
import de.kifo.common.music.GuildMusicManager;
import de.kifo.common.music.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static de.kifo.common.enums.exception.ExceptionType.NOT_IN_SPEECH_CHANNEL;
import static de.kifo.common.enums.exception.ExceptionType.NO_SONG_RUNNING;
import static de.kifo.common.enums.message.Message.MessageType.MESSAGE;
import static de.kifo.common.util.EmbedUtils.getEmbedMessageByText;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@CommandBase.BotCommand(name = "stop", description = "Beende das Abspielen von Musik.")
public class StopCommand extends CommandBase {

    @Inject
    private JavaBot javaBot;

    public StopCommand(@NotNull BotCommand command) {
        super(command);
    }

    @Override
    public void execute(Member member, TextChannel textChannel, List<OptionMapping> options, SlashCommandInteractionEvent event) throws CommandException {
        GuildVoiceState guildVoiceState = member.getVoiceState();

        if (isNull(guildVoiceState) || isNull(guildVoiceState.getChannel()) || isNull(guildVoiceState.getChannel().asVoiceChannel())) {
            throw new CommandException(NOT_IN_SPEECH_CHANNEL, event);
        }

        VoiceChannel voiceChannel = guildVoiceState.getChannel().asVoiceChannel();
        PlayerManager playerManager = javaBot.getPlayerManager();
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(event.getGuild());
        AudioPlayer audioPlayer = guildMusicManager.getTrackScheduler().getAudioPlayer();
        AudioManager audioManager = voiceChannel.getGuild().getAudioManager();

        if (audioManager.isConnected()) {
            if (nonNull(audioPlayer.getPlayingTrack())) {
                guildMusicManager.getTrackScheduler().getQueue().clear();
                guildMusicManager.getTrackScheduler().getAudioPlayer().stopTrack();
            }
            audioManager.closeAudioConnection();
            event.replyEmbeds(getEmbedMessageByText("Die Musik wurde beendet.", MESSAGE)).queue();
        } else {
            throw new CommandException(NO_SONG_RUNNING, event);
        }
    }

    @Override
    public void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event) {}
}
