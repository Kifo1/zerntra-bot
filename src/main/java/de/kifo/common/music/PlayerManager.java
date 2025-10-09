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
import de.kifo.JavaBot;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

import static com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers.registerRemoteSources;

public class PlayerManager {

    @Inject
    private JavaBot javaBot;

    private Map<Long, GuildMusicManager> guildMusicManagers = new HashMap<>();
    private AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();

    public PlayerManager() {
        YoutubeAudioSourceManager youtubeAudioSourceManager = new YoutubeAudioSourceManager();
        audioPlayerManager.registerSourceManager(youtubeAudioSourceManager);

        final String clientId = "e4f0b179b75443f2a782699b2547b7cd"; //Spotify client iD //TODO Safe is securely
        final String clientSecret = "d7fcdeb28f6b492599ee0073159788a1"; //Spotify client secret //TODO secure the secret
        final String spDc = "AQCc8j8H8Kjoth-pEYrNZNHlK5BDyWjM9_xI2e6dBqnB0LIWrathboI6izK28wtp96i8MhGrV10Vm81oMuKMBRp73UUG7J4AohEh8HKAC-2EK5S12Z4Dxa5XqgxRS4HoelEGz9ZOAxeNObdkPf-fpkoHTL7BfPSZSXdkKKanO71AqBxikyLPMisZSUw_uhueoGyblp8ZgIdgp38n-g";
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
                javaBot.getApi().updateSong(userId, audioTrack.getInfo().title);
                guildMusicManager.getTrackScheduler().queue(audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                //AudioTrack audioTrack = audioPlaylist.getTracks().get(0);
                //javaBot.getApi().updateSong(userId, audioTrack.getInfo().title);
                //guildMusicManager.getTrackScheduler().queue(audioTrack);
                if (audioPlaylist.isSearchResult()) {
                    AudioTrack firstTrack = audioPlaylist.getTracks().get(0);
                    javaBot.getApi().updateSong(userId, firstTrack.getInfo().title);
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
