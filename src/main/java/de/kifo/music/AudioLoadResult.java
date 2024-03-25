package de.kifo.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.kifo.music.commands.PlayCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.util.*;

import static java.lang.Thread.sleep;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

public class AudioLoadResult extends AudioEventAdapter implements AudioLoadResultHandler {

    private final String url;
    private final MusicController controller;
    private final Guild guild;
    public static HashMap<Guild, Queue<AudioTrack>> map = new HashMap<>();

    public AudioLoadResult(MusicController controller, String url, Guild guild) {
        this.controller = controller;
        this.url = url;
        this.guild = guild;
    }

    public AudioLoadResult(Guild guild) {
        this.controller = null;
        this.url = null;
        this.guild = guild;
    }

    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        if(!map.containsKey(guild)) {
            map.put(guild, new LinkedList<>());
        }

        map.get(guild).offer(audioTrack);

        if(controller.getPlayer().getPlayingTrack() == null) {
            nextSong(controller.getPlayer());
        } else {
            songAdded(audioTrack);
        }
    }

    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {
        if(!map.containsKey(guild)) {
            map.put(guild, new LinkedList<>());
        }

        map.get(guild).offer(audioPlaylist.getTracks().get(0));

        if(controller.getPlayer().getPlayingTrack() == null) {
            nextSong(controller.getPlayer());
        } else {
            songAdded(audioPlaylist.getTracks().get(0));
        }
    }

    @Override
    public void noMatches() {

    }

    @Override
    public void loadFailed(FriendlyException e) {

    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if(!map.get(guild).isEmpty()) {
            nextSong(player);
        }
        newSingleThreadExecutor().execute( () -> {
            try {
                sleep(60000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if(player.getPlayingTrack() == null) {
                if(guild.getAudioManager().getConnectedChannel() != null) {
                    guild.getAudioManager().closeAudioConnection();
                    //guild.getAudioManager().getConnectedChannel().asVoiceChannel().sendMessage("Wegen inaktivität verlasse ich jetzt den Sprachkanal.").queue();
                }
            }
        });
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.MAGENTA);
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

        //if(url.startsWith("https://www.youtube.com/watch?v=")) {
        //String videoID = url.replace("https://www.youtube.com/watch?v=", "");

        //InputStream file;
        //try{
        //  file = new URL("https://img.youtube.com/vi/" + videoID + "/hqdefault.jpg").openStream();
        //} catch(IOException e) { }
        //}

        TextChannel channel = PlayCommand.map.get(guild.getIdLong());
        channel.sendMessageEmbeds(builder.build()).queue();
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
    }

    private void nextSong(AudioPlayer player) {
        player.playTrack(map.get(guild).peek());
        map.get(guild).poll();
    }

    private void songAdded(AudioTrack track) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.MAGENTA);
        AudioTrackInfo info = track.getInfo();
        builder.setTitle(info.title + " von " + info.author + " wurde zur Playlist hinzugefügt.");

        TextChannel channel = PlayCommand.map.get(guild.getIdLong());
        channel.sendMessageEmbeds(builder.build()).queue();
    }
}