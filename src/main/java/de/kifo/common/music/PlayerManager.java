package de.kifo.common.music;

import com.github.topi314.lavasrc.mirror.DefaultMirroringAudioTrackResolver;
import com.github.topi314.lavasrc.spotify.SpotifySourceManager;
import com.google.inject.Inject;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.kifo.JavaBot;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers.registerRemoteSources;
import static de.kifo.JavaBot.messageService;
import static de.kifo.common.util.StringUtils.getTimeStringBySeconds;
import static java.awt.Color.MAGENTA;
import static java.lang.System.getenv;

public class PlayerManager {

    @Inject
    private JavaBot javaBot;

    private Map<Long, GuildMusicManager> guildMusicManagers = new HashMap<>();
    private AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();

    public PlayerManager() {
        YoutubeAudioSourceManager youtubeAudioSourceManager = new YoutubeAudioSourceManager();
        audioPlayerManager.registerSourceManager(youtubeAudioSourceManager);

        final String clientId = getenv("SPOTIFY_CLIENT_ID");
        final String clientSecret = getenv("SPOTIFY_CLIENT_SECRET");
        final String spDc = getenv("SPOTIFY_SPCD");
        SpotifySourceManager spotifySourceManager = new SpotifySourceManager(clientId, clientSecret, spDc, "DE", ignored -> audioPlayerManager, new DefaultMirroringAudioTrackResolver(null));
        audioPlayerManager.registerSourceManager(spotifySourceManager);

        registerRemoteSources(audioPlayerManager, com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager.class);
        registerRemoteSources(audioPlayerManager, SpotifySourceManager.class);
    }

    public GuildMusicManager getGuildMusicManager(Guild guild) {
        return guildMusicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            GuildMusicManager musicManager = new GuildMusicManager(audioPlayerManager, guild);
            guild.getAudioManager().setSendingHandler(musicManager.getAudioForwarder());
            return musicManager;
        });
    }

    public CompletableFuture<MessageEmbed> play(Guild guild, String trackURL, Long userId) {
        GuildMusicManager guildMusicManager = getGuildMusicManager(guild);
        CompletableFuture<MessageEmbed> future = new CompletableFuture<>();

        audioPlayerManager.loadItemOrdered(guildMusicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                AudioTrackInfo audioTrackInfo = audioTrack.getInfo();
                javaBot.getApi().updateSong(userId, audioTrackInfo.title);
                future.complete(getMessageEmbedBySongQueueState(guildMusicManager.getTrackScheduler().queue(audioTrack), audioTrackInfo));
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                if (audioPlaylist.isSearchResult()) {
                    AudioTrack firstTrack = audioPlaylist.getTracks().get(0);
                    AudioTrackInfo audioTrackInfo = firstTrack.getInfo();
                    javaBot.getApi().updateSong(userId, firstTrack.getInfo().title);
                    future.complete(getMessageEmbedBySongQueueState(guildMusicManager.getTrackScheduler().queue(firstTrack), audioTrackInfo));
                } else {
                    audioPlaylist.getTracks().forEach(track -> guildMusicManager.getTrackScheduler().queue(track));
                    future.complete(messageService.message("Playlist wurde geladen."));
                }
            }

            @Override
            public void noMatches() {
                future.complete(messageService.error("Das Lied konnte nicht gefunden werden."));
            }

            @Override
            public void loadFailed(FriendlyException e) {
                future.complete(messageService.error("Das Lied konnte nicht geladen werden."));
            }
        });

        return future;
    }

    private MessageEmbed getMessageEmbedBySongQueueState(boolean skipQueue, AudioTrackInfo audioTrackInfo) {
        return skipQueue ?
                new EmbedBuilder()
                        .setColor(MAGENTA)
                        .setTitle("Jetzt läuft: " + audioTrackInfo.title)
                        .addField(audioTrackInfo.author, "[" + audioTrackInfo.title + "](" + audioTrackInfo.uri + ")", false)
                        .addField("Länge", audioTrackInfo.isStream ? ":red_circle: Stream" : getTimeStringBySeconds(audioTrackInfo.length / 1000), true)
                        .build() :
                messageService.message(audioTrackInfo.title + " wurde zur Songlist hinzugefügt.");
    }
}
