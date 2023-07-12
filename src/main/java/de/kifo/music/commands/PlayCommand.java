package de.kifo.music.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import de.kifo.Main;
import de.kifo.commands.types.ServerCommand;
import de.kifo.database.utils.UserCommandsDataUtils;
import de.kifo.database.utils.UserDataUtils;
import de.kifo.database.utils.UserErrorsDataUtils;
import de.kifo.music.AudioLoadResult;
import de.kifo.music.MusicController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.util.HashMap;

public class PlayCommand implements ServerCommand {

    public static HashMap<Long, TextChannel> map = new HashMap<>();

    @Override
    public void executeCommand(Member member, TextChannel channel, Message message) {
        UserCommandsDataUtils.addPlayCommand(member.getGuild().getIdLong(), member.getUser().getIdLong(), 1);
        String[] args = message.getContentRaw().split(" ");

        if(args.length > 1) {
            GuildVoiceState state;

            if((state = member.getVoiceState()) != null) {
                VoiceChannel vc = null;
                if(state.getChannel() != null) {
                    vc = state.getChannel().asVoiceChannel();
                }

                if(vc != null) {
                    MusicController controller = Main.getInstance().getPlayerManager().getController(vc.getGuild().getIdLong());
                    AudioPlayer player = controller.getPlayer();
                    AudioPlayerManager apm = Main.getInstance().getAudioPlayerManager();
                    AudioManager manager = vc.getGuild().getAudioManager();
                    manager.openAudioConnection(vc);

                    StringBuilder stringBuilder = new StringBuilder();
                    for(int i = 1; i< args.length; i++) {
                        stringBuilder.append(args[i] + " ");
                    }

                    String url = stringBuilder.toString().trim();
                    if(!url.startsWith("http")) {
                        url = "ytsearch:" + url + " audio";
                        channel.sendMessage("Suche nach deinen Begriffen...").queue();
                    }

                    apm.loadItem(url, new AudioLoadResult(controller, url, controller.getGuild()));

                    map.put(vc.getGuild().getIdLong(), channel);
                } else {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setDescription("Du musst in einem Sprachkanal sein. " + member.getAsMention());
                    builder.setColor(Color.MAGENTA);
                    channel.sendMessageEmbeds(builder.build()).queue();
                    UserErrorsDataUtils.addWrongUsageError(member.getGuild().getIdLong(), member.getUser().getIdLong(), 1);
                }
            } else {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setDescription("Du musst in einem Sprachkanal sein. " + member.getAsMention());
                builder.setColor(Color.MAGENTA);
                channel.sendMessageEmbeds(builder.build()).queue();
                UserErrorsDataUtils.addWrongUsageError(member.getGuild().getIdLong(), member.getUser().getIdLong(), 1);
            }
        } else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setDescription("Verwende bitte \"!play <Url/Suchbegriffe>\". " + member.getAsMention());
            builder.setColor(Color.MAGENTA);
            channel.sendMessageEmbeds(builder.build()).queue();
            UserErrorsDataUtils.addWrongUsageError(member.getGuild().getIdLong(), member.getUser().getIdLong(), 1);
        }
    }
}