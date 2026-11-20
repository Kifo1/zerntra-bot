package de.kifo.common.music;

import com.github.topi314.lavasrc.mirror.DefaultMirroringAudioTrackResolver;
import com.github.topi314.lavasrc.spotify.SpotifySourceManager;
import com.google.inject.Inject;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterfaceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.kifo.JavaBot;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.AndroidVrWithThumbnail;
import dev.lavalink.youtube.clients.ClientOptions;
import dev.lavalink.youtube.clients.IosWithThumbnail;
import dev.lavalink.youtube.clients.MusicWithThumbnail;
import dev.lavalink.youtube.clients.WebEmbeddedWithThumbnail;
import dev.lavalink.youtube.clients.WebWithThumbnail;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.http.client.config.RequestConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers.registerRemoteSources;
import static de.kifo.JavaBot.messageService;
import static java.lang.System.getenv;

@Slf4j
public class PlayerManager {

    @Inject
    private JavaBot javaBot;

    private final Map<Long, GuildMusicManager> guildMusicManagers = new HashMap<>();
    private final AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();

    public PlayerManager() {
        ClientOptions webOptions = ClientOptions.DEFAULT.copy();
        webOptions.setPlayback(false);

        ClientOptions iosOptions = ClientOptions.DEFAULT.copy();
        iosOptions.setSearching(false);

        YoutubeAudioSourceManager youtubeAudioSourceManager = new YoutubeAudioSourceManager(
                true,
                new MusicWithThumbnail(),
                new WebWithThumbnail(webOptions),
                new IosWithThumbnail(iosOptions),
                new AndroidVrWithThumbnail(),
                new WebEmbeddedWithThumbnail()
        );
        audioPlayerManager.registerSourceManager(youtubeAudioSourceManager);

        final String clientId = getenv("SPOTIFY_CLIENT_ID");
        final String clientSecret = getenv("SPOTIFY_CLIENT_SECRET");
        final String spDc = getenv("SPOTIFY_SPCD");
        HttpInterfaceManager spotifyTokenHttpManager = HttpClientTools.createDefaultThreadLocalManager();
        spotifyTokenHttpManager.configureRequests(config -> RequestConfig.copy(config)
                .setConnectTimeout(15000)
                .setSocketTimeout(30000)
                .build());
        SpotifySourceManager spotifySourceManager = new SpotifySourceManager(
                clientId,
                clientSecret,
                true,
                "http://host.docker.internal:8090/api/token",
                spDc,
                "US",
                ignored -> audioPlayerManager,
                new DefaultMirroringAudioTrackResolver(null)
        ) {
            @Override
            public HttpInterface getHttpInterface() {
                return spotifyTokenHttpManager.getInterface();
            }
        };
        audioPlayerManager.registerSourceManager(spotifySourceManager);

        registerRemoteSources(audioPlayerManager, com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager.class);
        registerRemoteSources(audioPlayerManager, SpotifySourceManager.class);
    }

    public GuildMusicManager getGuildMusicManager(Guild guild) {
        return guildMusicManagers.computeIfAbsent(guild.getIdLong(), guildId -> {
            GuildMusicManager musicManager = new GuildMusicManager(audioPlayerManager, guild);
            guild.getAudioManager().setSendingHandler(musicManager.getAudioForwarder());
            return musicManager;
        });
    }

    public CompletableFuture<MessageEmbed> play(Guild guild, String trackURL, Long userId) {
        GuildMusicManager guildMusicManager = getGuildMusicManager(guild);
        TrackScheduler trackScheduler = guildMusicManager.getTrackScheduler();
        CompletableFuture<MessageEmbed> future = new CompletableFuture<>();

        audioPlayerManager.loadItemOrdered(guildMusicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                AudioTrackInfo audioTrackInfo = audioTrack.getInfo();
                javaBot.getApi().updateSong(userId, audioTrackInfo.title, audioTrackInfo.uri);
                if (trackScheduler.queue(audioTrack)) {
                    future.complete(messageService.message("The song starts now."));
                } else {
                    future.complete(messageService.message(audioTrackInfo.title + " has been added to the songlist."));
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                if (audioPlaylist.isSearchResult()) {
                    AudioTrack firstTrack = audioPlaylist.getTracks().getFirst();
                    AudioTrackInfo audioTrackInfo = firstTrack.getInfo();
                    javaBot.getApi().updateSong(userId, audioTrackInfo.title, audioTrackInfo.uri);
                    if(trackScheduler.queue(firstTrack)) {
                        future.complete(messageService.message("The song starts now."));
                    } else {
                        future.complete(messageService.message(audioTrackInfo.title + " has been added to the songlist."));
                    }
                } else {
                    audioPlaylist.getTracks().forEach(trackScheduler::queue);
                    future.complete(messageService.message("The playlist starts now."));
                }
            }

            @Override
            public void noMatches() {
                future.complete(messageService.error("This song could not be found."));
            }

            @Override
            public void loadFailed(FriendlyException e) {
                log.error("Failed to load track {}: {}", trackURL, e.getMessage(), e);
                future.complete(messageService.error("This song could not be found."));
            }
        });

        return future;
    }
}
