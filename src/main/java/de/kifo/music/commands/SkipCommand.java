package de.kifo.music.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.kifo.Main;
import de.kifo.commands.types.ServerCommand;
import de.kifo.database.utils.UserCommandsDataUtils;
import de.kifo.database.utils.UserErrorsDataUtils;
import de.kifo.music.AudioLoadResult;
import de.kifo.music.MusicController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class SkipCommand implements ServerCommand {

    @Override
    public void executeCommand(Member member, TextChannel channel, Message message) {
        UserCommandsDataUtils.addSkipCommand(member.getGuild().getIdLong(), member.getUser().getIdLong(), 1);
        String[] args = message.getContentRaw().split(" ");
        GuildVoiceState state;

        if((state = member.getVoiceState()) != null) {
            VoiceChannel vc = null;
            if(state.getChannel() != null) {
                vc = state.getChannel().asVoiceChannel();
            } else {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setDescription("Du musst in einem Sprachkanal sein. " + member.getAsMention());
                builder.setColor(Color.MAGENTA);
                channel.sendMessageEmbeds(builder.build()).queue();
                UserErrorsDataUtils.addWrongUsageError(member.getGuild().getIdLong(), member.getUser().getIdLong(), 1);
            }

            if(vc != null) {
                MusicController controller = Main.getInstance().playerManager.getController(vc.getGuild().getIdLong());
                AudioPlayer player = controller.getPlayer();

                if(player.getPlayingTrack() != null) {
                    if(args.length == 1) {
                        player.stopTrack();
                        channel.sendMessage("Das Lied wird übersprungen...").queue();
                    } else if(args.length == 2) {
                        int number;
                        try {
                            number = Integer.parseInt(args[1]);
                        } catch (Exception e) {
                            EmbedBuilder builder = new EmbedBuilder();
                            builder.setColor(Color.MAGENTA);
                            builder.setDescription("Verwende bitte /skip oder /skip <Nummer>");
                            builder.setFooter("Verwende !playlist, um alle Lieder mit der jeweiligen Nummer angezeigt zu bekommen.");
                            channel.sendMessageEmbeds(builder.build()).queue();
                            UserErrorsDataUtils.addWrongUsageError(member.getGuild().getIdLong(), member.getUser().getIdLong(), 1);
                            return;
                        }

                        LinkedList<AudioTrack> tracks = (LinkedList<AudioTrack>) AudioLoadResult.map.get(vc.getGuild());
                        if(tracks.size() < number) {
                            EmbedBuilder builder = new EmbedBuilder();
                            builder.setColor(Color.MAGENTA);
                            builder.setDescription("Diese Nummer existiert nicht.");
                            builder.setFooter("Verwende !playlist, um alle Lieder mit der jeweiligen Nummer angezeigt zu bekommen.");
                            channel.sendMessageEmbeds(builder.build()).queue();
                            UserErrorsDataUtils.addWrongUsageError(member.getGuild().getIdLong(), member.getUser().getIdLong(), 1);
                            return;
                        }

                        AudioTrack track = tracks.get(number - 1);
                        AudioLoadResult.map.get(vc.getGuild()).remove(track);
                        EmbedBuilder builder = new EmbedBuilder();
                        builder.setColor(Color.MAGENTA);
                        builder.setDescription("" + track.getInfo().title + " von " + track.getInfo().author + " wurde aus der Playlist entfernt.");
                        channel.sendMessageEmbeds(builder.build()).queue();
                    } else {
                        EmbedBuilder builder = new EmbedBuilder();
                        builder.setColor(Color.MAGENTA);
                        builder.setDescription("Verwende bitte /skip oder /skip <Nummer>");
                        builder.setFooter("Verwende !playlist, um alle Lieder mit der jeweiligen Nummer angezeigt zu bekommen.");
                        channel.sendMessageEmbeds(builder.build()).queue();
                        UserErrorsDataUtils.addWrongUsageError(member.getGuild().getIdLong(), member.getUser().getIdLong(), 1);
                    }
                } else {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.MAGENTA);
                    builder.setDescription("Es läuft im Moment kein Lied. " + member.getAsMention());
                    channel.sendMessageEmbeds(builder.build()).queue();
                    UserErrorsDataUtils.addWrongUsageError(member.getGuild().getIdLong(), member.getUser().getIdLong(), 1);
                }
            }
        }
    }
}