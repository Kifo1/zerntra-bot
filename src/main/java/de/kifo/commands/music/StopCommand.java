package de.kifo.commands.music;

import com.google.inject.Inject;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import de.kifo.JavaBot;
import de.kifo.commands.handle.CommandBase;
import de.kifo.common.exceptions.CommandException;
import de.kifo.common.music.GuildMusicManager;
import de.kifo.common.music.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static de.kifo.JavaBot.messageService;
import static de.kifo.common.enums.exception.ExceptionType.NOT_IN_SPEECH_CHANNEL;
import static de.kifo.common.enums.exception.ExceptionType.NO_SONG_RUNNING;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@CommandBase.BotCommand(name = "stop", description = "Stop playing music.")
public class StopCommand extends CommandBase {

    @Inject
    private JavaBot javaBot;

    public StopCommand(@NotNull BotCommand command) {
        super(command);
    }

    @Override
    public void execute(Member member, TextChannel textChannel, List<OptionMapping> options, SlashCommandInteractionEvent event) throws CommandException {
        GuildVoiceState guildVoiceState = member.getVoiceState();
        Guild guild = event.getGuild();

        if (isNull(guildVoiceState) || isNull(guildVoiceState.getChannel())) {
            throw new CommandException(NOT_IN_SPEECH_CHANNEL, event);
        }

        PlayerManager playerManager = javaBot.getPlayerManager();
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(guild);
        AudioPlayer audioPlayer = guildMusicManager.getTrackScheduler().getAudioPlayer();
        AudioManager audioManager = guild.getAudioManager();

        if (audioManager.isConnected()) {
            if (nonNull(audioPlayer.getPlayingTrack())) {
                guildMusicManager.getTrackScheduler().getQueue().clear();
                guildMusicManager.getTrackScheduler().getAudioPlayer().stopTrack();
            }
            audioManager.closeAudioConnection();
            event.deferReply().queue();
            messageService.sendMessageAndDestroy(event, "The music has been stopped.", 60);
        } else {
            throw new CommandException(NO_SONG_RUNNING, event);
        }
    }

    @Override
    public void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event) {
        throw new UnsupportedOperationException("Stop command does not have arguments for auto complete.");
    }
}
