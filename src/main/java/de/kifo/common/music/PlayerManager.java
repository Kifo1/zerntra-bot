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
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

import static com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers.registerRemoteSources;
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

    public void play(Guild guild, String trackURL, Long userId) {
        GuildMusicManager guildMusicManager = getGuildMusicManager(guild);
        audioPlayerManager.loadItemOrdered(guildMusicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                AudioTrackInfo trackInfo = audioTrack.getInfo();
                javaBot.getApi().updateSong(userId, trackInfo.title, trackInfo.uri);
                guildMusicManager.getTrackScheduler().queue(audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                if (audioPlaylist.isSearchResult()) {
                    AudioTrack firstTrack = audioPlaylist.getTracks().get(0);
                    AudioTrackInfo trackInfo = firstTrack.getInfo();
                    javaBot.getApi().updateSong(userId, trackInfo.title, trackInfo.uri);
                    guildMusicManager.getTrackScheduler().queue(firstTrack);
                } else {
                    audioPlaylist.getTracks().forEach(track -> {
                        guildMusicManager.getTrackScheduler().queue(track);
                    });
                }
            }

            @Override
            public void noMatches() {
                System.out.println("not matches");
            }

            @Override
            public void loadFailed(FriendlyException e) {
                System.out.println("load failed");
            }
        });
    }
}
