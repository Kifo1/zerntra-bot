package de.kifo.common.util;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.kifo.JavaBot;
import de.kifo.common.music.GuildMusicManager;
import de.kifo.common.music.PlayerManager;
import de.kifo.common.music.TrackScheduler;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.List;

import static de.kifo.JavaBot.buttonService;
import static de.kifo.common.enums.button.ButtonType.*;
import static java.awt.Color.MAGENTA;
import static java.lang.Math.ceil;
import static java.util.stream.IntStream.rangeClosed;

@UtilityClass
public class SonglistUtils {

    private static final JavaBot javaBot = JavaBot.javaBot;

    public static MessageEmbed getSonglistPageEmbed(Guild guild, int page) {
        PlayerManager playerManager = javaBot.getPlayerManager();
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(guild);
        TrackScheduler trackScheduler = guildMusicManager.getTrackScheduler();

        List<AudioTrack> queuedSongList = new ArrayList<>(trackScheduler.getQueue());
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(MAGENTA);

        builder.setTitle("Page " + page + ":");
        rangeClosed(((page - 1) * 10), (9 + (page - 1) * 10)).forEach(index -> {
            if (index <= queuedSongList.size() - 1) {
                int trackNumber = index + 1;
                AudioTrack song = queuedSongList.get(index);
                builder.appendDescription("Song " + trackNumber + ": " + song.getInfo().title + "\n");
            }
        });
        builder.setFooter("Use \"/skip <number>\", to remove the song from the songlist.");
        return builder.build();
    }

    public static ActionRow getSonglistPageActionRow(Guild guild, int page) {
        int pages = getCurrentPageAmount(guild);
        ActionRow actionRow;
        if (page == 1) {
            actionRow = ActionRow.of(
                    buttonService.getButtonByType(DELETE).getButton(),
                    buttonService.getButtonByType(NEXT_PAGE).getButton());
        } else if (page == pages) {
            actionRow = ActionRow.of(
                    buttonService.getButtonByType(PREVIOUS_PAGE).getButton(),
                    buttonService.getButtonByType(DELETE).getButton());
        } else {
            actionRow = ActionRow.of(
                    buttonService.getButtonByType(PREVIOUS_PAGE).getButton(),
                    buttonService.getButtonByType(DELETE).getButton(),
                    buttonService.getButtonByType(NEXT_PAGE).getButton());
        }
        return actionRow;
    }

    public static int getCurrentPageAmount(Guild guild) {
        return (int) ceil((double) javaBot.getPlayerManager().getGuildMusicManager(guild).getTrackScheduler().getQueue().size() / 10);
    }

    public static int extractPage(String text) {
        String pageStr = text.replace("Page ", "").replace(":", "").trim();
        return Integer.parseInt(pageStr);
    }
}
