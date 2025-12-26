package de.kifo.common.button.song;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import de.kifo.common.button.handle.ButtonBase;
import de.kifo.common.music.GuildMusicManager;
import de.kifo.common.music.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import static de.kifo.commands.music.PlayCommand.map;
import static de.kifo.common.enums.button.ButtonType.STOP_MUSIC;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@ButtonBase.BotButton(buttonType = STOP_MUSIC)
public class StopMusicButton extends ButtonBase {

    public StopMusicButton(BotButton botButton) {
        super(botButton);
    }

    @Override
    public void onClick(ButtonInteractionEvent event) {
        Guild guild = event.getGuild();

        if (isNull(guild)) return;

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
            map.remove(guild.getIdLong());
        }
        event.deferEdit().queue();
    }
}
