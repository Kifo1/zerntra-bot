package de.kifo.common.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer audioPlayer;
    private final Guild guild;
    private BlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<>();

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        AudioTrackInfo info = track.getInfo();
        String url = info.uri;

        long sekunden = info.length/1000;
        long minuten = sekunden/60;
        long stunden = minuten/60;
        minuten %= 60;
        sekunden %= 60;

        map.get(guild.getIdLong()).sendMessageEmbeds(new EmbedBuilder()
                .setColor(MAGENTA)
                .setTitle("Jetzt läuft: " + info.title)
                .addField(info.author, "[" + info.title +"](" + url + ")", false)
                .addField("Länge", info.isStream ? ":red_circle: Stream" : (stunden > 0 ? stunden + "h " : "") + minuten + "min " + sekunden + "s", true)
                .build()).queue();
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

            if(isNull(player.getPlayingTrack()) && nonNull(guild.getAudioManager().getConnectedChannel())) {
                guild.getAudioManager().closeAudioConnection();
            }
        });
    }

    public void queue(AudioTrack audioTrack) {
        if (!audioPlayer.startTrack(audioTrack, true)) {
            queue.offer(audioTrack);
        }
    }
}
