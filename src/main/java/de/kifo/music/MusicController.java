package de.kifo.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import de.kifo.JavaBot;
import lombok.Data;
import net.dv8tion.jda.api.entities.Guild;

import javax.inject.Inject;

@Data
public class MusicController {

    @Inject
    private JavaBot javaBot;

    private final Guild guild;
    private final AudioPlayer player;

    public MusicController(Guild guild) {
        this.guild = guild;
        this.player = javaBot.getAudioPlayerManager().createPlayer();

        this.guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
        this.player.addListener(new AudioLoadResult(this.guild));
        this.player.setVolume(15);
        this.player.setFrameBufferDuration(1000000000);
    }
}