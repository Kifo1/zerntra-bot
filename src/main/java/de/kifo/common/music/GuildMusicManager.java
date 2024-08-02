package de.kifo.common.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;

@Getter
public class GuildMusicManager {

    private TrackScheduler trackScheduler;
    private AudioForwarder audioForwarder;

    public GuildMusicManager(AudioPlayerManager audioPlayerManager, Guild guild) {
        AudioPlayer audioPlayer = audioPlayerManager.createPlayer();
        this.trackScheduler = new TrackScheduler(audioPlayer, guild);
        this.audioForwarder = new AudioForwarder(audioPlayer);

        audioPlayer.addListener(this.trackScheduler);
    }
}
