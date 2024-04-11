package de.kifo.common.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static de.kifo.commands.music.PlayCommand.map;
import static java.awt.Color.MAGENTA;
import static java.lang.Thread.sleep;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

@Getter
public class TrackScheduler extends AudioEventAdapter {

    private AudioPlayer audioPlayer;
    private BlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<>();
    private Guild guild;

    TrackScheduler(AudioPlayer audioPlayer, Guild guild) {
        this.audioPlayer = audioPlayer;
        this.guild = guild;
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(MAGENTA);
        AudioTrackInfo info = track.getInfo();
        builder.setTitle("Jetzt läuft: " + info.title);

        long sekunden = info.length/1000;
        long minuten = sekunden/60;
        long stunden = minuten/60;
        minuten %= 60;
        sekunden %= 60;

        String url = info.uri;
        builder.addField(info.author, "[" + info.title +"](" + url + ")", false);
        builder.addField("Länge", info.isStream ? ":red_circle: Stream" : (stunden > 0 ? stunden + "h " : "") + minuten + "min " + sekunden + "s", true);

        map.get(guild.getIdLong()).sendMessageEmbeds(builder.build()).queue();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        audioPlayer.startTrack(queue.poll(), false);

        newSingleThreadExecutor().execute(() -> {
            try {
                sleep(60000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if(isNull(player.getPlayingTrack())) {
                if(nonNull(guild.getAudioManager().getConnectedChannel())) {
                    guild.getAudioManager().closeAudioConnection();
                }
            }
        });
    }

    public void queue(AudioTrack audioTrack) {
        if (!audioPlayer.startTrack(audioTrack, true)) {
            queue.offer(audioTrack);
        }
    }
}
