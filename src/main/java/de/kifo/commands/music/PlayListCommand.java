package de.kifo.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.kifo.JavaBot;
import de.kifo.commands.handle.CommandBase;
import de.kifo.common.music.AudioLoadResult;
import de.kifo.common.music.MusicController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;

import static java.awt.Color.MAGENTA;
import static java.util.Objects.isNull;
import static java.util.stream.IntStream.rangeClosed;

@CommandBase.Command(name = "playlist", description = "Zeigt die aktuelle Playlist an.")
public class PlayListCommand extends CommandBase {

    @Inject
    private JavaBot javaBot;

    public PlayListCommand(@NotNull Command command) {
        super(command);
    }

    @Override
    public void execute(Member member, TextChannel textChannel, List<OptionMapping> options, SlashCommandInteractionEvent event) {
        GuildVoiceState guildVoiceState = member.getVoiceState();

        if (isNull(guildVoiceState) || isNull(guildVoiceState.getChannel()) || isNull(guildVoiceState.getChannel().asVoiceChannel())) {
            event.reply("Du musst in einem Sprachkanal sein.").queue(); //TODO Replace with embed (EmbedBuilder builder = new EmbedBuilder())
            return;
        }

        VoiceChannel voiceChannel = guildVoiceState.getChannel().asVoiceChannel();
        MusicController controller = javaBot.getPlayerManager().getController(voiceChannel.getGuild().getIdLong());
        AudioPlayer audioPlayer = controller.getPlayer();

        if (isNull(audioPlayer.getPlayingTrack())) {
            event.reply("Es läuft im Moment kein Lied.").queue();
            return;
        }

        LinkedList<AudioTrack> tracks = (LinkedList<AudioTrack>) AudioLoadResult.map.get(textChannel.getGuild());
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(MAGENTA);

        if (!tracks.isEmpty()) {
            rangeClosed(1, tracks.size()).forEach(trackNumber -> builder.addField(trackNumber + "- ", tracks.get(trackNumber - 1).getInfo().title, false));
            builder.setFooter("Verwende \"/skip <Nummer>\", um das gewünschte Lied aus der Playlist zu entfernen.");
            event.replyEmbeds(builder.build());
        } else {
            event.reply("Die Playlist ist aktuell leer.").queue();
        }
    }

    @Override
    public void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event) {}
}
