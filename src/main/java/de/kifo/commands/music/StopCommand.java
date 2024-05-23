package de.kifo.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import de.kifo.JavaBot;
import de.kifo.commands.handle.CommandBase;
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

import javax.inject.Inject;
import java.util.List;

import static de.kifo.common.util.EmbedUtils.MessageType.ERROR;
import static de.kifo.common.util.EmbedUtils.MessageType.MESSAGE;
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
    public void execute(Member member, TextChannel textChannel, List<OptionMapping> options, SlashCommandInteractionEvent event) {
        GuildVoiceState guildVoiceState = member.getVoiceState();

        if (isNull(guildVoiceState) || isNull(guildVoiceState.getChannel()) || isNull(guildVoiceState.getChannel().asVoiceChannel())) {
            event.replyEmbeds(javaBot.getEmbedUtils().getEmbedMessageByText("Du musst in einem Sprachkanal sein.", ERROR)).queue();
            return;
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
            event.replyEmbeds(javaBot.getEmbedUtils().getEmbedMessageByText("Die Musik wurde beendet.", MESSAGE)).queue();
        } else {
            event.replyEmbeds(javaBot.getEmbedUtils().getEmbedMessageByText("Es läuft im Moment kein Lied.", ERROR)).queue();
        }
    }

    @Override
    public void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event) {}
}
