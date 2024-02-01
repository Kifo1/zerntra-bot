package de.kifo.music.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.kifo.commands.types.ServerCommand;
import de.kifo.music.AudioLoadResult;
import de.kifo.music.MusicController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.LinkedList;

import static de.kifo.Main.getInstance;
import static de.kifo.database.utils.UserErrorsDataUtils.addWrongUsageError;
import static java.awt.Color.MAGENTA;

public class PlayListCommand implements ServerCommand {

    @Override
    public void executeCommand(Member member, TextChannel channel, Message message) throws InterruptedException {
        //TODO Add data to database
        GuildVoiceState state;

        if((state = member.getVoiceState()) == null)
            return;

        VoiceChannel vc = null;
        if(state.getChannel() != null) {
            vc = state.getChannel().asVoiceChannel();
        } else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setDescription("Du musst in einem Sprachkanal sein. " + member.getAsMention());
            builder.setColor(MAGENTA);
            channel.sendMessageEmbeds(builder.build()).queue();
            addWrongUsageError(member.getGuild().getIdLong(), member.getUser().getIdLong(), 1);
        }

        if(vc != null) {
            MusicController controller = getInstance().getPlayerManager().getController(vc.getGuild().getIdLong());
            AudioPlayer player = controller.getPlayer();

            if(player.getPlayingTrack() != null ) {
                LinkedList<AudioTrack> tracks = (LinkedList<AudioTrack>) AudioLoadResult.map.get(channel.getGuild());
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(MAGENTA);

                if(tracks.isEmpty()) {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setColor(MAGENTA);
                    embedBuilder.setDescription("Die Playlist ist aktuell leer. " + member.getAsMention());
                    channel.sendMessageEmbeds(embedBuilder.build()).queue();
                    addWrongUsageError(member.getGuild().getIdLong(), member.getUser().getIdLong(), 1);
                    return;
                }


                int trackNumber = 1;
                for(AudioTrack track : tracks) {
                    builder.addField("" + trackNumber + " - ", track.getInfo().title, false);
                    trackNumber += 1;
                }

                builder.setFooter("Verwende !skip <Nummer>, um das gewünschte Lied aus der Playlist zu entfernen.");
                channel.sendMessageEmbeds(builder.build()).queue();
            } else {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(MAGENTA);
                builder.setDescription("Es läuft im Moment kein Lied. " + member.getAsMention());
                channel.sendMessageEmbeds(builder.build()).queue();
                addWrongUsageError(member.getGuild().getIdLong(), member.getUser().getIdLong(), 1);
            }
        }
    }
}
