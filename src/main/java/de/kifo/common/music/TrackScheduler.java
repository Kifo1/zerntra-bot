package de.kifo.common.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.kifo.common.services.MessageService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

import static de.kifo.JavaBot.messageService;
import static de.kifo.commands.music.PlayCommand.map;
import static de.kifo.common.util.StringUtils.getProgressBar;
import static java.awt.Color.MAGENTA;
import static java.lang.Thread.sleep;
import static java.util.Collections.shuffle;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.concurrent.TimeUnit.SECONDS;

@Getter
@RequiredArgsConstructor
public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer audioPlayer;
    private final Guild guild;
    private final BlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<>();
    private ScheduledExecutorService scheduler;

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        MessageService.UpdatableMessage updatableMessage = map.get(guild.getIdLong());

        stopSchedulerIfRunning();

        scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable task = () -> {
            AudioTrack t = audioPlayer.getPlayingTrack();
            if (isNull(t) || t.getPosition() >= t.getDuration() || !t.isSeekable()) {
                stopSchedulerIfRunning();
                return;
            }

            MessageEmbed embed = getMessageEmbedBySongQueueState(true, t);
            updatableMessage.update(embed);
        };

        scheduler.scheduleAtFixedRate(task, 0, 1, SECONDS);
    }

    private void stopSchedulerIfRunning() {
        if (nonNull(scheduler) && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (isQueueEmpty()) {
            MessageService.UpdatableMessage updatableMessage = map.get(guild.getIdLong());
            updatableMessage.getMessage().delete().queue();
            map.remove(guild.getIdLong());
        } else {
            audioPlayer.startTrack(queue.poll(), false);
        }

        newSingleThreadExecutor().execute(() -> {
            try {
                sleep(60000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if(isNull(player.getPlayingTrack()) && nonNull(guild.getAudioManager().getConnectedChannel())) {
                guild.getAudioManager().closeAudioConnection();
                map.remove(guild.getIdLong());
            }
        });
    }

    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }

    public boolean isPlaying() {
        return nonNull(audioPlayer.getPlayingTrack());
    }

    public boolean queue(AudioTrack audioTrack) {
        if (audioPlayer.startTrack(audioTrack, true)) {
            return true;
        }
        queue.offer(audioTrack);
        return false;
    }

    public void shuffleQueue() {
        List<AudioTrack> songList = new ArrayList<>(queue);
        shuffle(songList);
        queue.clear();
        queue.addAll(songList);
    }

    public MessageEmbed getMessageEmbedBySongQueueState(boolean isNowPlaying, AudioTrack audioTrack) {
        AudioTrackInfo audioTrackInfo = audioTrack.getInfo();
        return isNowPlaying ?
                new EmbedBuilder()
                        .setColor(MAGENTA)
                        .setTitle(audioTrackInfo.title)
                        .setDescription(getProgressBar(audioTrack.getPosition(), audioTrack.getDuration(), 20))
                        .setThumbnail("https://img.youtube.com/vi/" + audioTrackInfo.identifier + "/hqdefault.jpg")
                        .build() :
                messageService.message(audioTrackInfo.title + " wurde zur Songlist hinzugefügt.");
    }
}
