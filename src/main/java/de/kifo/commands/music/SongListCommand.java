package de.kifo.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.kifo.JavaBot;
import de.kifo.commands.handle.CommandBase;
import de.kifo.common.music.GuildMusicManager;
import de.kifo.common.music.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static de.kifo.common.util.EmbedUtils.MessageType.ERROR;
import static java.awt.Color.MAGENTA;
import static java.util.Objects.isNull;

@CommandBase.BotCommand(name = "songlist", description = "Zeigt die aktuelle Song Liste an.")
public class SongListCommand extends CommandBase {

    @Inject
    private JavaBot javaBot;

    public SongListCommand(@NotNull BotCommand command) {
        super(command);
    }

    @Override
    public void execute(Member member, TextChannel textChannel, List<OptionMapping> options, SlashCommandInteractionEvent event) {
        GuildVoiceState guildVoiceState = member.getVoiceState();

        if (isNull(guildVoiceState) || isNull(guildVoiceState.getChannel()) || isNull(guildVoiceState.getChannel().asVoiceChannel())) {
            event.replyEmbeds(javaBot.getEmbedUtils().getEmbedMessageByText("Du musst in einem Sprachkanal sein.", ERROR)).queue();
            return;
        }

        PlayerManager playerManager = javaBot.getPlayerManager();
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(event.getGuild());
        AudioPlayer audioPlayer = guildMusicManager.getTrackScheduler().getAudioPlayer();

        if (isNull(audioPlayer.getPlayingTrack())) {
            event.reply("Es läuft im Moment kein Lied.").queue();
            return;
        }

        BlockingQueue<AudioTrack> tracks = guildMusicManager.getTrackScheduler().getQueue();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(MAGENTA);

        if (!tracks.isEmpty()) {
            AtomicInteger trackNumber = new AtomicInteger(1);
            tracks.forEach(track -> {
                builder.addField("Lied " + trackNumber + ": ", track.getInfo().title, false);
                trackNumber.getAndIncrement();
            });
            builder.setFooter("Verwende \"/skip <Nummer>\", um das gewünschte Lied aus der Liste zu entfernen.");
            event.replyEmbeds(builder.build()).queue();
        } else {
            event.reply("Die Liste ist aktuell leer.").queue();
        }
    }

    @Override
    public void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event) {}
}
