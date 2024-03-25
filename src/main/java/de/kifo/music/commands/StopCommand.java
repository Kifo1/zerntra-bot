package de.kifo.music.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import de.kifo.oldcommands.types.ServerCommand;
import de.kifo.music.MusicController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.managers.AudioManager;

import static de.kifo.Main.getInstance;
import static de.kifo.database.utils.UserCommandsDataUtils.addStopCommand;
import static de.kifo.database.utils.UserErrorsDataUtils.addWrongUsageError;
import static de.kifo.music.AudioLoadResult.map;
import static java.awt.Color.MAGENTA;

public class StopCommand implements ServerCommand {

    @Override
    public void executeCommand(Member member, TextChannel channel, Message message) {
        addStopCommand(member.getGuild().getIdLong(), member.getUser().getIdLong(), 1);
        GuildVoiceState state;

        if((state = member.getVoiceState()) != null) {
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
                AudioManager manager = vc.getGuild().getAudioManager();
                AudioPlayer player = controller.getPlayer();

                if(player.getPlayingTrack() != null) {
                    map.get(channel.getGuild()).clear();
                    player.stopTrack();
                    manager.closeAudioConnection();
                    message.addReaction(Emoji.fromFormatted("U+1F44C")).queue();
                } else if(manager.isConnected()) {
                    manager.closeAudioConnection();
                    message.addReaction(Emoji.fromFormatted("U+1F44C")).queue();
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
}