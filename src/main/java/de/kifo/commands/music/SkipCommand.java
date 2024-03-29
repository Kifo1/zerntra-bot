package de.kifo.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.kifo.JavaBot;
import de.kifo.commands.handle.CommandBase;
import de.kifo.common.music.AudioLoadResult;
import de.kifo.common.music.MusicController;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static java.lang.String.valueOf;
import static java.util.Objects.isNull;
import static java.util.stream.IntStream.rangeClosed;
import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;

@CommandBase.Command(name = "skip", description = "Überspringe ein Lied aus der aktuellen Playlist.", hasOptions = true)
public class SkipCommand extends CommandBase {

    @Inject
    private JavaBot javaBot;

    public SkipCommand(@NotNull Command command) {
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

        if (isNull(options.get(0))) {
            audioPlayer.stopTrack();
            event.reply("Das Lied wird übersprungen...").queue(); //TODO Replace with embed (EmbedBuilder builder = new EmbedBuilder())
        } else {
            LinkedList<AudioTrack> tracks = (LinkedList<AudioTrack>) AudioLoadResult.map.get(voiceChannel.getGuild());
            int number = options.get(0).getAsInt();

            if (tracks.size() > number) {
                AudioTrack track = tracks.get(number - 1);
                AudioLoadResult.map.get(voiceChannel.getGuild()).remove(track);
                event.reply(track.getInfo().title + " von " + track.getInfo().author + " wurde aus der Playlist entfernt.").queue(); //TODO Replace with embed (EmbedBuilder builder = new EmbedBuilder())
            } else {
                event.reply("Dieser Index existiert nicht.").queue(); //TODO Replace with embed (EmbedBuilder builder = new EmbedBuilder())
                //TODO Add footer to embed with text "Verwende !playlist, um alle Lieder mit der jeweiligen Nummer angezeigt zu bekommen."
            }
        }
    }

    @Override
    public void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event) {
        List<net.dv8tion.jda.api.interactions.commands.Command.Choice> options = new ArrayList<>();

        if (optionName.equalsIgnoreCase("index")) {
            rangeClosed(1, AudioLoadResult.map.get(event.getChannel().asGuildMessageChannel().getGuild()).size())
                    .forEach(number -> options.add(new net.dv8tion.jda.api.interactions.commands.Command.Choice(valueOf(number), number)));
            event.replyChoices(options).queue();
        }
    }

    @Override
    public List<OptionData> getOptions() {
        return of(new OptionData(INTEGER, "index", "Index des Liedes, welches übersprungen werden soll", false, true));
    }
}
